import tweepy

tweets = tweepy.api.public_timeline()
for tweet in tweets :
	print tweet