#! /usr/bin/env python

# Exit codes accepted by postfix
#  from postfix-2.0.16/src/global/sys_exits.h
EX_USAGE    = 64    # command line usage error
EX_NOUSER   = 67    # addressee unknown
EX_SOFTWARE = 70    # internal software error
EX_TEMPFAIL = 75    # temporary failure

import sys
import urllib
import urllib2

authName = sys.argv[1]
authPassword = sys.argv[2]
recipient = sys.argv[3]
url = sys.argv[4]
message = sys.stdin.read()

payload = urllib.urlencode({"authName":authName, "authPassword":authPassword, "recipient":recipient, "message":message})

try:
	response = urllib2.urlopen(url, payload)
except urllib2.HTTPError, e:
	if e.code == 599:
		sys.exit(EX_NOUSER)
	else:
		sys.exit(EX_TEMPFAIL)
