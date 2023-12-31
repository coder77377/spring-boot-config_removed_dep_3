/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.nicoll.boot.config.diff;

import net.nicoll.boot.config.loader.AetherDependencyResolver;

/**
 * @author Stephane Nicoll
 */
public class ConfigDiffSample {

	public static void main(String[] args) throws Exception {
		ConfigDiffGenerator configDiffGenerator = new ConfigDiffGenerator(
				AetherDependencyResolver.withAllRepositories());
		ConfigDiffResult configDiffResult = configDiffGenerator.generateDiff("3.0.0-RC1", "3.0.0-SNAPSHOT");
		ConfigDiffFormatter formatter = new AsciiDocConfigDiffFormatter();
		System.out.println(formatter.formatDiff(configDiffResult));
	}

}
