app.controller("indexController",function ($scope,$controller,loginService) {
    $scope.showLoginName = function () {
        loginService.showLoginName().success(
            function (data) {
                $scope.loginName = data.loginName;
            }
        );
    }

});