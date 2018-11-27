app.service('brandService',function ($http) {
    /*1、查询所有品牌*/
    this.findAllBrand = function () {
        return $http.get("../brand/findAll.do");
    }

    /*2、分页查询品牌列表*/
    this.findBrandByPage = function (page,rows) {
        return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);
    }

    /*3、根据id得到品牌*/
    this.findById = function (id) {
        return $http.get('../brand/findById.do?id='+id);
    }

    /*4、根据条件查询*/
    this.search = function (page,rows,entity) {
        return $http.post('../brand/search.do?page='+page+"&rows="+rows, entity);
    }

    /*5、根据id删除品牌*/
    this.deleteByIds = function (ids) {
        return $http.get("../brand/deleteByIds.do?ids=" +ids);
    }

    /*6、添加品牌*/
    this.addBrand = function (entity) {
       return $http.post('../brand/addBrand.do',entity)
    }

    /*7、修改品牌信息*/
    this.updateBrand = function (entity) {
       return $http.post('../brand/updateBrand.do',entity)
    }
    /*8、获取brand列表*/
    this.selectOptionList=function(){
        return $http.get('../brand/selectOptionList.do');
    }


});