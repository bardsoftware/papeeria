FROM dockerfile/java:oracle-java8
RUN curl "https://bootstrap.pypa.io/get-pip.py" | python3.4
RUN git clone https://github.com/bardsoftware/papeeria -b named_entity_recognition
RUN pip3.4 install -r /data/papeeria/build/requirements.txt
RUN cat /data/papeeria/build/alias >> ~/.bashrc