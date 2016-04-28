#!/bin/bash
ARG_P=-pP0,P1,P2,P3,P8,P5,P6,P7
if [ $1 -eq 1 ]; then
	ARG_P=-pP8,P9,P10,P11,P12,P13,P14,P15
fi
if [ $1 -eq 2 ]; then
	ARG_P=-pP16,P17,P18,P19,P20,P21,P22,P23
fi
if [ $1 -eq 3 ]; then
	ARG_P=-pP24,P25,P26,P27,P28,P29,P30,P31
fi
if [ $1 -eq 4 ]; then
	ARG_P=-pP32,P33,P36,P37,P38,P39 
#P34,P35,
fi
if [ $1 -eq 5 ]; then
	ARG_P=-pP40,P41,P42,P43,P44,P45,P46,P47
fi
sudo ~/bin/runTest.sh ${ARG_P}