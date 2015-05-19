FROM java:8 

RUN apt-get update && apt-get -y install python3
RUN curl "https://bootstrap.pypa.io/get-pip.py" | python3.4
RUN git clone https://github.com/bardsoftware/papeeria -b named_entity_recognition
RUN pip3.4 install -r papeeria/build/requirements.txt
RUN chmod +x papeeria/build/commands/*  && mv papeeria/build/commands/* /usr/bin
RUN mkdir /data && mv papeeria /data
RUN build
