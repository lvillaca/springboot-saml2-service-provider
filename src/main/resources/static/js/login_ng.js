var app = angular.module('loginApp', []);
app.controller('loginCtrl', function($scope, $http) {



    $scope.logout =function() {
		      window.location.href = "/logout";
                };


    function getLogin(element) {
            element.user = '';
             $http({
                method : 'GET',
                withCredentials: true,
                url : '/hasLogged'
                }).then(function successCallback(response) {
                                   element.user = response.data.user;
                               },function errorCallback(error) {
                                   element.user = '';
                               });
            return true;
    };


    getLogin($scope);


    $scope.setModal = function() {
        $http({
                method : 'GET',
                withCredentials: true,
                url : '/details'
                }).then(function successCallback(response) {
                                   $scope.userdetail = response.data;
                               },function errorCallback(error) {
                                   $scope.userdetail = '';
                               });

	$('#updateModal').modal('show');
    };

    
});
