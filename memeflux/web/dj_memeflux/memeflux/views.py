
from memeflux.models import *
from django.shortcuts import render_to_response, get_object_or_404

def springs(request):
	pass
	# all_springs = Spring.objects.all()
	# return render_to_response('path/to/listview.html', {'all_springs': all_springs})

def spring(request, source_id):
	pass
	# spring = get_object_or_404(Spring, pk=spring_id)
	# return render_to_response('path/to/view.html', {'spring': spring})
