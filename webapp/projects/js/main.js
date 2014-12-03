jQuery(document).ready(function() 
    { 
        var scope = new Scope();
        var em = new EnterMedia(scope);

        scope.add("entermedia", em );
        jAngular.addScope("emsearch", scope);
    });

var Query = function() {
    var out = { 
        field : "description",
        operator: "contains",
        values: null 
    };
    return out;
}

var EnterMedia = function(scope) {
    var out = {
        search: function() {
            var keyword = $('#searchinput').val();
            var query = new Query();
            query.values = [keyword];
            $.ajax({
                  contentType: 'text/plain',
                  type: 'POST',
                  //processData: false,
                  url: 'http://demo.entermediasoftware.com/entermedia/services/json/search/data/asset?catalogid=media/catalogs/public',
                  data: '{ "query" : [' + JSON.stringify(query) + '] }',
                  success: 
                        function(data) {
                                scope.results = JSON.parse(data).results;
                                jAngular.render('#result-view', scope);
                        }
            });
        }
    }
    return out;
}
