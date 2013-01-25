app.factory('question', ['$resource', function($resource) {
  return $resource('/api/questions/:id', {}, {
    'create' : {
      method : 'PUT'
    },
    'get' : {
      method: 'GET',
      isArray: false
    },
    'query' : {
      method: 'GET',
      isArray: true
    }
  });
}]);

app.controller('QuestionCtrl', ['$scope', '$routeParams', 'question', function($scope, $routeParams, question) {
  $scope.newQuestion = {
    title: "",
    play: "",
    lang: "",
    author: "TODO:getAuthor"
  }

  if ($routeParams.id) {
    $scope.question = question.get({id: $routeParams.id},
      function() {

      },
      function() {

      }
    )
  }

  $scope.create = function() {
    question.create({}, angular.toJson($scope.newQuestion));
  }
}])