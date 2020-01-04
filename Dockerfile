FROM java:8
ARG V1=1
ARG V2=0
ARG V3=7
ARG V4=43

ARG VERSIONMINUS=${V1}-${V2}-${V3}-${V4}
ARG VERSIONDOT=${V1}.${V2}.${V3}.${V4}
RUN wget https://github.com/yccheok/jstock/releases/download/release_${VERSIONMINUS}/jstock-${VERSIONDOT}-bin.zip && unzip jstock-${VERSIONDOT}-bin.zip && rm jstock-${VERSIONDOT}-bin.zip
WORKDIR /jstock
RUN chmod +x jstock.sh
ENTRYPOINT ./jstock.sh
