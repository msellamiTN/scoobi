/**
 * Copyright 2011,2012 National ICT Australia Limited
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
package impl
package mapreducer

import org.apache.hadoop.fs.Path

import ChannelOutputFormat._
import testing.mutable.UnitSpecification
import com.nicta.scoobi.io.text.TextFileSink

class ChannelOutputFormatSpec extends UnitSpecification {
  "Channels determine result files for a given job run" >> {
    "ch1-2/ is a result directory for a sink with tag 1 and sink id 2" >> {
      val sink = TextFileSink(".")
      sink.isSinkResult(1)(new Path(s"ch1-${sink.id}/"))
    }
  }

}
