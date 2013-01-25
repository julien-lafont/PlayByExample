app.controller('GistCtrl', ['$scope', function($scope) {

}])

app.factory('gist', ['$resource', function($resource) {
  return $resource('https://api.github.com/gists/:id', {}, {
    'get' : {
      method : 'GET',
      isArray : false
    }
  });
}]);