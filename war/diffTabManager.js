 function putSourceCodeContent(left,right) {
    	var bfpHeight = $('#bugfixContainer').height();
    	
    	var comp = $('#compare');
    	//--------
    		comp.mergely({
    			width: 'auto',
    			height: (window.innerHeight - bfpHeight)*0.8,
    			ignorews: true,
    			cmsettings: { readOnly: true, lineNumbers: true },
    			 lhs: function(setValue) {
    				$('#compare').mergely('lhs', left);
    				},
    			rhs: function(setValue) {
    					$('#compare').mergely('rhs',right);
    				}
    				
   		});//workarround
    		comp.mergely('lhs', left);
        	comp.mergely('rhs', right);
        	
	}