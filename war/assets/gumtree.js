if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
    return this.slice(0, str.length) == str;
  };
}

function getMappedElement(eltId) {
	if (eltId.startsWith("mapping-src")) {
		return eltId.replace("src","dst");  	 	
  	}
  	else {
  		return eltId.replace("dst","src");
  	}
}

function isSrc(eltId) {
	return eltId.startsWith("mapping-src");
}

$("span.mv.token, span.token.upd").click(
	function() {
		$("span.mv.token, span.token.upd").removeClass("selected");
		var eltId = $(this).attr("id");
		var refEltId = getMappedElement(eltId);
		$("#" + refEltId).addClass("selected");
		$(this).addClass("selected");
		var sel = "#dst";
		if (isSrc(refEltId)) var sel = "#src";
		
		$div = $(sel);
		$span = $("#" + refEltId);     		
		$div.scrollTop($div.scrollTop() + $span.position().top - $div.height()/2 + $span.height()/2);
	}
)

$("span.token").hover(
	function () {
		$(this).tooltip('show');
	}, 
	function () {
		$(this).tooltip('hide');
	}
);