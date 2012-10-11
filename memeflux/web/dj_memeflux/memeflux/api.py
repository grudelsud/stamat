from django.contrib.auth.models import User

from tastypie import fields
from tastypie.resources import ModelResource
from tastypie.authorization import DjangoAuthorization
from tastypie.authentication import ApiKeyAuthentication

from memeflux.models import *

class UserResource(ModelResource):
	class Meta:
		queryset = User.objects.all()
		resource_name = 'user'
		fields = ['id', 'username', 'first_name', 'last_name', 'last_login']
		allowed_methods = ['get']
		authorization = DjangoAuthorization()
		# authentication = ApiKeyAuthentication()

class SubstanceResource(ModelResource):
	user = fields.ToOneField(UserResource, 'user')
	parent = fields.ToOneField('self', 'parent', null=True, blank=True)

	class Meta:
		queryset = Substance.objects.all()
		resource_name = 'substance'
		authorization = DjangoAuthorization()
		# authentication = ApiKeyAuthentication()

class DropResource(ModelResource):
	user = fields.ToOneField(UserResource, 'user')
	substances = fields.ToManyField(SubstanceResource, 'substances', null=True, blank=True)

	class Meta:
		queryset = Drop.objects.all()
		resource_name = 'drop'
		authorization = DjangoAuthorization()
		# authentication = ApiKeyAuthentication()

class BlobResource(ModelResource):
	user = fields.ToOneField(UserResource, 'user')
	substances = fields.ToManyField(SubstanceResource, 'substances', null=True, blank=True)

	class Meta:
		queryset = Blob.objects.all()
		resource_name = 'blob'
		authorization = DjangoAuthorization()
		# authentication = ApiKeyAuthentication()

class SpringResource(ModelResource):
	users = fields.ToManyField(UserResource, 'users')
	substances = fields.ToManyField(SubstanceResource, 'substances', null=True, blank=True)
	drops = fields.ToManyField(DropResource, 'drops', null=True, blank=True)
	blobs = fields.ToManyField(BlobResource, 'blobs', null=True, blank=True)

	class Meta:
		queryset = Spring.objects.all()
		resource_name = 'spring'
		authorization = DjangoAuthorization()
		# authentication = ApiKeyAuthentication()
