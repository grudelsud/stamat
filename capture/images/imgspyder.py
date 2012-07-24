import constants

import argparse
import ConfigParser
import MySQLdb
import urllib
import os
import datetime
import Image
import logging

from boto.s3.connection import S3Connection
from boto.s3.key import Key
import boto

class Spider:
	"reads db table feeditemmedia and store stuff on s3"

	def __init__(self):
		pass

	def init_db(self, data):
		self.db_con = MySQLdb.connect(host=data['host'], user=data['user'], passwd=data['passwd'], unix_socket=data['unix_socket'], db=data['db'])

	def init_aws(self, data):
		self.s3_con = S3Connection(data['aws_key_id'], data['aws_secret_key'])
		self.s3_bucket = self.s3_con.get_bucket(data['bucket'])
		# self.s3_con = boto.connect_s3()

	def db_fetch_urls(self):
		cur = self.db_con.cursor(MySQLdb.cursors.DictCursor)
		cur.execute("SELECT id, url FROM feeditemmedia WHERE type='image' AND flags=0 ORDER BY created DESC LIMIT 1000")
		return cur.fetchall()

	def db_update_flag_fetched(self, data):
		cur = self.db_con.cursor()
		cur.execute("UPDATE feeditemmedia SET flags=%s, abs_path=%s, hash=%s, width=%s, height=%s WHERE id=%s", (data['flags'], data['abs_path'], data['hash'], data['width'], data['height'], data['id']))
		return cur.rowcount

	def get_content_from_web(self, url):
		# if we ever need to explore a wget solution
		# os.system('wget %s -a log.log' % url)
		hash = datetime.datetime.now().strftime('%s.%f')
		urllib.urlretrieve(url, hash)
		return hash

	def store_s3(self, filename):
		k = Key(self.s3_bucket)
		k.key = filename
		k.set_contents_from_filename(filename)
		k.set_acl('public-read')

class Main:

	def __init__(self, args):
		self.args = args

		config = ConfigParser.RawConfigParser(allow_no_value=True)
		config.readfp(args.config)

		self.database = dict(config.items('database'))
		self.aws = dict(config.items('aws'))

	def run(self):
		s = Spider()
		s.init_db(self.database)
		s.init_aws(self.aws)

		rows = s.db_fetch_urls()
		for row in rows:
			hash = s.get_content_from_web(row['url'])

			try:
				img = Image.open(hash)
				if img.format is not None:
					row['hash'] = hash + '.png'
					row['width'] = img.size[0]
					row['height'] = img.size[1]
					row['flags'] = constants.DOWNLOADED
					row['abs_path'] = 'https://s3.amazonaws.com/'+self.aws['bucket']+'/'
					img.save(row['hash'])
					s.store_s3(row['hash'])
					os.remove(hash)
					os.remove(row['hash'])

			except IOError:
				row['hash'] = 'nope'
				row['width'] = 0
				row['height'] = 0
				row['flags'] = constants.INVALID
				logging.error("ioerror reading file: %s" % (hash))

			s.db_update_flag_fetched(row)

if __name__ == '__main__':

	logging.basicConfig(filename='spider.log',level=logging.WARNING)

	# setup args and entry points
	parser = argparse.ArgumentParser(description="Fetch & store images")
	parser.add_argument('-c', '--config', type=argparse.FileType('r'), default='settings.cfg', help="full path of config file to be used, defaults to settings.cfg")

	args = parser.parse_args()
	m = Main(args)
	m.run()
