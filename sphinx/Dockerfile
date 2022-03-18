FROM galilee/sphinx-build

WORKDIR /root/src
RUN pip install six
COPY ./docs/apidocs /root/src/apidocs
COPY ./sphinx /root/src
RUN /usr/bin/python3 commonlinks_to_conf.py
RUN cp /root/src/ext/commonlinks.py /usr/local/lib/python3.9/dist-packages/sphinx/ext
RUN mkdir -p /root/docs/static
RUN sphinx-build -W -b html /root/src /root/docs

FROM nginx

RUN rm -rf /usr/share/nginx/html/* && ln -s /root/docs /usr/share/nginx/html
COPY --from=0 /root/docs /root/docs
