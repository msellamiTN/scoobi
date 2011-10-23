/**
  * Copyright 2011 National ICT Australia Limited
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.nicta.scoobi

import org.apache.hadoop.mapred.{Reducer => HReducer, _}


/** Hadoop Combiner class for an MSCR. */
class MscrCombiner[V] extends HReducer[TaggedKey, TaggedValue, TaggedKey, TaggedValue] {

  private var combiners: Map[Int, TaggedCombiner[_]] = _
  private var tv: TaggedValue = _

  def configure(conf: JobConf) = {
    combiners = DistributedObject.pullObject(conf, "scoobi.combiners").asInstanceOf[Map[Int, TaggedCombiner[_]]]
    tv = conf.getMapOutputValueClass.newInstance.asInstanceOf[TaggedValue]
  }

  def reduce(key: TaggedKey,
             values: java.util.Iterator[TaggedValue],
             output: OutputCollector[TaggedKey, TaggedValue],
             reporter: Reporter) = {

    val tag = key.tag
    val valuesStream = Stream.continually(if (values.hasNext) values.next else null).takeWhile(_ != null)

    if (combiners.contains(tag)) {
      /* Only perform combining if one is available for this tag. */
      val combiner = combiners(tag).asInstanceOf[TaggedCombiner[V]]

      /* Convert Iterator[TaggedValue] to Iterable[V]. */
      val untaggedValues = valuesStream.map(_.get(tag).asInstanceOf[V]).toIterable

      /* Do the combining. */
      val reduction = untaggedValues.tail.foldLeft(untaggedValues.head)(combiner.combine)
      tv.set(tag, reduction)

      output.collect(key, tv)
    } else {
      /* If no combiner for this tag, TK-TV passes through. */
      valuesStream.foreach { value => output.collect(key, value) }
    }
  }

  def close() = {
  }
}
