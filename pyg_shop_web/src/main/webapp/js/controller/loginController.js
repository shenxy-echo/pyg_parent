app.controller("loginController",function ($scope,$controller,loginService) {
    $controller('baseController',{$scope:$scope});//继承
    $scope.showLoginName = function () {
        loginService.showLoginName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }
});