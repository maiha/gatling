/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.http.request

import java.io.File

import io.gatling.core.util.CacheHelper

import io.gatling.core.config.GatlingConfiguration.configuration
import io.gatling.core.config.Resource
import io.gatling.core.session.Expression
import io.gatling.core.util.IO._
import io.gatling.core.validation.Validation

object RawFileBodies {

  lazy val Cache = CacheHelper.newCache[String, Validation[File]](configuration.http.rawFileBodiesCacheMaxCapacity)

  def cached(path: String) =
    if (configuration.http.rawFileBodiesCacheMaxCapacity > 0)
      Cache.getOrElseUpdate(path, Resource.requestBody(path).map(_.jfile))
    else
      Resource.requestBody(path).map(_.jfile)

  def asFile(filePath: Expression[String]): Expression[File] = session =>
    for {
      path <- filePath(session)
      file <- cached(path)
      validatedFile <- file.validateExistingReadable
    } yield validatedFile
}
