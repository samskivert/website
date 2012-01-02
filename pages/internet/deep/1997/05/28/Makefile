DIRS = \
	wordgame \
	dist \
	dclient \
	dserver

all:
	@for i in $(DIRS); do make -C $$i all ; done

clean:
	@for i in $(DIRS); do make -C $$i clean ; done

publish: dclient
	/opt/MRBtrans/castanet/transmitter/bin/publish -host dev.go2net.com:3113 `pwd`

remote-publish: dclient
	@tar -C `pwd` -c -f - . | ssh www1 "rm -rf /home/mdb/channels/wordgame; mkdir /home/mdb/channels/wordgame; /usr/local/bin/tar -x -C /home/mdb/channels/wordgame -f - ; /opt/MRBtrans/castanet/transmitter/bin/publish -host www1.go2net.com:2020 /home/mdb/channels/wordgame"
