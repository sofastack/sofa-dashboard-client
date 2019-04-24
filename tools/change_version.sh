#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

shellDir=$(cd "$(dirname "$0")"; pwd)

shopt -s expand_aliases
if [ ! -n "$1" ] ;then
	echo "Please enter a version"
 	exit 1	
else
  	echo "The version is $1 !"
fi

if [ `uname` == "Darwin" ] ;then
 	echo "This is OS X"
 	alias sed='sed -i ""'
else
 	echo "This is Linux"
 	alias sed='sed -i'
fi

cd $shellDir/..
echo "Change version in registry-parent ===>"
sed "/<project /,/<name>/ s/<version>[^\$].*<\/version>/<version>$1<\/version>/" ./pom.xml

echo "Change version in registry-client-all ===>"
sed "/<project /,/<dependencies/ s/<version>[^\$].*<\/version>/<version>$1<\/version>/" ./client/all/pom.xml

echo "Change version in subproject pom ===>"
for filename in `find . -name "pom.xml" -maxdepth 4`;do
  if [ $filename == "./client/all/pom.xml" ]; then
     continue
  fi
	echo "Deal with $filename"
	sed "/<parent>/,/<\/parent>/ s/<version>[^\$].*<\/version>/<version>$1<\/version>/" $filename
done

#TODO
#echo "Change version in server shell ===>"