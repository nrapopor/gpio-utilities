#!/bin/bash
cd ~/projects
if [ ! -d ${project.build.finalName} ]; then
	tar -xvf ${project.build.finalName}-bin-dist.tar.gz
fi
cd ${project.build.finalName}
sudo java -jar ${project.build.finalName}.jar $*

