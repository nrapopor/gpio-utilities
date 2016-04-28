#!/bin/bash
cd ~/projects
if [ -d ${project.build.finalName} ]; then
	rm -r ${project.build.finalName}
fi
tar -xvf ${project.build.finalName}-bin-dist.tar.gz

