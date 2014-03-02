/**
 * Created by brent on 3/2/14.
 */
angular.module('GitHacks', [ ])

    .service('clojureService', ['$http', function($http, fbURL) {
        var service = {};

        var url="/";

        service.generateCommits = function(username, cb) {
            $http.post("/commits", {username: username})
                .success(cb)
            .error(function(d) {console.log(d)});
        }
    }])

    .controller('GitCtrl', ['$scope', 'clojureService', function($scope, clojureService) {
        $scope.username = "bbaumgar";

        $scope.goGoGenerate = function() {
            clojureService.generateCommits($scope.username, function(d) {
                console.log(d);
            })
        }

    }]);

