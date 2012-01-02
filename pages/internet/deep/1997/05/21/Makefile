DIRS = \
	chat \
	dist \
	dclient \
	dserver

all:
	@for i in $(DIRS); do make -C $$i all ; done

clean:
	@for i in $(DIRS); do make -C $$i clean ; done
