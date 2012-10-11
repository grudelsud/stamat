from memeflux.models import Substance
from memeflux.models import Drop
from memeflux.models import Blob
from memeflux.models import Spring
from memeflux.models import Torrent
from memeflux.models import Puddle

from django.contrib import admin

class SubstanceAdmin(admin.ModelAdmin):
	list_display = ('name', 'slug', 'essence', 'parent', 'count')
	search_fields = ['name', 'slug', 'essence']

class SpringAdmin(admin.ModelAdmin):
	list_display = ('title', 'url', 'fetch', 'created')
	search_fields = ['title', 'url']
	date_hierarchy = 'created'

admin.site.register(Substance, SubstanceAdmin)

admin.site.register(Drop)
admin.site.register(Blob)

admin.site.register(Spring, SpringAdmin)
admin.site.register(Torrent)
admin.site.register(Puddle)
