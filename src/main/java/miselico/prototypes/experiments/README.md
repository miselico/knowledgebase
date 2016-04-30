Benchmarks
==========


This package contains several benchmarks which were performed for the paper: Knowledge Representation on the Web revisited: Tools for Prototype Based Ontologies.

Standalone Experiments are concerned with the speed of an embedded KB.
BenchmarkClient and server benchmark a distributed setup.
Datasets contains methods for creating synthetic KBs.
MyKnowledgeBase shows examples of how a KB can be manipulated.

virtual machine configuration
------------------------

###VM creation###

* vmbuilder kvm ubuntu --dest=/srv/kvm/orginalProtoServer --suite=trusty --flavour=virtual --arch=amd64 --libvirt=qemu:///system --mem=4096 --rootsize=5000 --swapsize=0 --cpus=4 --ip=192.168.122.200 --mask=255.255.255.0 --gw=192.168.122.1 --hostname=orginalProtoServer --addpkg=acpid --addpkg linux-image-generic --addpkg=ssh --user=ubuntu --pass=***** --proxy=http://192.168.122.1:3142
* install java: Download and extract jdk-8u92

e.g.
* 2 servers protoServer1 and protoServer2 on addresses 192.168.122.200 and 192.168.122.201
* 1 client protoClient1 on address 192.168.122.210


###VM configuration###

This configuration has to happen on each machine. Note that these setting  will be gone after reboot.

Configure netem and traffic shaping. 
    * tc qdisc add dev eth0 root handle 1:0 netem delay 286ms 22ms distribution normal loss 0.04% 
    * tc qdisc add dev eth0 parent 1:1 handle 10: tbf rate 1024kbit burst 10kb limit 100mbit 
Check using
    * tc qdisc show dev eth0
Remove the qdisk using    
    * tc qdisc del dev eth0 root