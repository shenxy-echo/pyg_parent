//品牌控制层
app.controller('brandController' ,function($scope,$controller,brandService){
    $controller('baseController',{$scope:$scope});//继承
    //读取列表数据绑定到表单中
    $scope.findAllBrand=function(){
        brandService.findAllBrand().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    $scope.findBrandByPage = function (page,rows) {
        brandService.findBrandByPage(page,rows).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;
            }
        );
    }

    $scope.deleteByIds = function () {
        brandService.deleteByIds($scope.selectIds).success(
            function (data) {
                if(data.success){
                    $scope.reloadList();
                }else{
                    alert(data.message);
                }
            }
        );
    }

    $scope.editBrand = function () {
        var operationService;
        if($scope.entity.id != null){
            operationService = brandService.updateBrand($scope.entity);
        }else{
            operationService = brandService.addBrand($scope.entity);
        }
        operationService.success(
            function (data) {
                if(data.success){
                    $scope.findBrandByPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
                }else{
                    alert(data.message);
                }
            }
        );
    }

    /*根据id获取品牌，并回显到弹出框中*/
    $scope.findById = function (id) {
        brandService.findById(id).success(
            function (data) {
                $scope.entity = data;
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象
    //条件查询
    $scope.search=function(page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.paginationConf.totalItems=response.total;//总记录数
                $scope.list=response.rows;//给列表变量赋值
            }
        );
    }

});