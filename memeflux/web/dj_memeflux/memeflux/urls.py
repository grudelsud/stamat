from django.conf.urls import patterns, include, url
from tastypie.api import Api

from memeflux.api import *

v1_api = Api(api_name='v1')
v1_api.register(UserResource())
v1_api.register(SubstanceResource())
v1_api.register(DropResource())
v1_api.register(BlobResource())
v1_api.register(SpringResource())

urlpatterns = patterns('',
	url(r'^api/', include(v1_api.urls)),

	# url(r'^spring/$', 'memeflux.views.springs'),
	# url(r'^spring/(?P<spring_id>\d+)/$', 'memeflux.views.spring'),
)