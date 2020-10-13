$(function () {
    $('#configTable').bootstrapTable({
        url: '/ftpconfig/list',
        contentType: "application/x-www-form-urlencoded",
        method: 'post',
        toolbar: '#toolbar',
        uniqueId: "id",
        pagination: true,    //是否显示分页
        toolbarAlign: "right", //工具栏对齐方式
        sidePagination: "server", //客户端分页“client” 服务器分页“server"
        search: false, //是否显示表格搜索功能，此搜索是客户端搜索，不进去服务器
        pageNumber: 1,
        pageSize: 10,
        pageList: [5, 10, 15, 20],
        smartDisplay: false,
        sortable: true,  //是否启用排序
        sortOrder: "asc",  //排序方式
        showColumns: true, //是否显示列选择按钮
        showRefresh: true,  //是否刷新按钮
        clickToSelect: true, //是否点击选择行
        checkbox: true,
        height: 670,
        striped: true, //是否启用隔行变色
        queryParams: function queryParams(params) {
            var temp = {
                pageNumber: params.offset,
                pageSize: params.limit
            }
            return temp;
        },
        columns: [
            {
                field: 'id',
                title: 'ID'
            }, {
                field: 'bankCode',
                title: '银行名称',
                formatter: bankFormat
            }, {
                field: 'accName',
                title: '账户名称',
                cellStyle: formatTableUnit,
                formatter: paramsMatter
            }, {
                field: 'accNo',
                title: '账号',
                cellStyle: formatTableUnit,
                formatter: paramsMatter
            },
            {
                field: 'ftpHost',
                title: 'ftp地址',
                cellStyle: formatTableUnit,
                formatter: paramsMatter
            },
            {
                field: 'ftpPort',
                title: 'ftp端口',
                cellStyle: formatTableUnit,
                formatter: paramsMatter
            },
            {
                field: 'ftpUser',
                title: 'ftp用户名'
            },
            {
                field: 'ftpPass',
                title: 'ftp密码'
            },
            {
                field: 'remotePath',
                title: 'ftp远程目录'
            },
            {
                field: 'operate',
                title: '操作',
                formatter: actionFormatter //自定义方法，添加操作按钮
            },
        ],
        rowStyle: function (row, index) {
            var classesArr = ['success', 'info'];
            var strclass = "";
            if (index % 2 === 0) {//偶数行
                strclass = classesArr[0];
            } else {//奇数行
                strclass = classesArr[1];
            }
            return {classes: strclass};
        },//隔行变色
    });
    $('#form_add').bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            accName: {
                message: '账户名称验证失败',
                validators: {
                    notEmpty: {
                        message: '账户名称不能为空'
                    }
                }
            },
            accNo: {
                validators: {
                    notEmpty: {
                        message: '账号不能为空'
                    },
                    regexp: {
                        regexp: /^[0-9_]+$/,
                        message: '账号只能包含数字'
                    }
                }
            },
            ftpHost: {
                validators: {
                    notEmpty: {
                        message: 'ftp地址不能为空'
                    }
                }
            },
            ftpPort: {
                validators: {
                    notEmpty: {
                        message: 'ftp端口号不能为空'
                    },
                    regexp: {
                        regexp: /^[0-9_]+$/,
                        message: 'ftp端口号只能包含数字'
                    }
                }
            }, ftpUser: {
                validators: {
                    notEmpty: {
                        message: 'ftp用户名不能为空'
                    }
                }
            }, ftpPass: {
                validators: {
                    notEmpty: {
                        message: 'ftp密码不能为空'
                    }
                }
            }, remotePath: {
                validators: {
                    notEmpty: {
                        message: 'ftp远程目录不能为空'
                    }
                }
            }
        }
    });
    toastr.options.positionClass = 'toast-top-center';
    $('#datetimepicker').datetimepicker({
        format: 'yyyymmdd',
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0
    });
});

function actionFormatter(value, row, index) {
    var result = "";
    result += "<button style='cursor: pointer' class='btn btn-primary' data-toggle='modal' data-target='#edit' onclick='upConfig(" + index + ")'>修改</button>&nbsp;&nbsp;";
    result += "<button style='cursor: pointer' class='btn btn-danger' data-toggle='modal' data-target='#del' onclick='delConfig(" + index + ")'>删除</button>&nbsp;&nbsp;";
    return result;
}

function bankFormat(value, row, index) {
    if (row.bankCode === 'icbc') {
        return "中国工商银行"
    } else if (row.bankCode === 'rcb') {
        return "农村信用社";
    } else if (row.bankCode === 'psbc') {
        return "中国及邮政储蓄银行";
    }
}

function paramsMatter(value, row, index) {
    var a;
    if (value == null || value == '' || value === undefined) {
        return;
    }
    if (value.length > 50) {
        a = value.substr(0, 50) + "...";
    } else {
        a = value;
    }
    var span = document.createElement('span');
    span.setAttribute('title', value);
    span.innerHTML = a;
    return span.outerHTML;
}

function formatTableUnit(value, row, index) {
    return {
        css: {
            "white-space": 'nowrap',
            "text-overflow": 'ellipsis',
            "overflow": 'hidden'
        }
    }
}

function delConfig(index) {
    var info = $("#configTable").bootstrapTable('getData')[index];
    Ewin.confirm({message: "确认要删除选择的数据吗？"}).on(function (e) {
        if (!e) {
            return;
        }
        $.ajax({
            url: '/ftpconfig/del',
            type: "post",
            data: {
                "id": info.id
            },
            success: function (data) {
                if (data.code === 200) {
                    toastr.success('删除数据成功');
                    $("button[name=refresh]").click();
                } else {
                    toastr.error(data.message);
                    $("button[name=refresh]").click();
                }

            }
        })
    })
};
var $addbtn = $("#btn_add");
$addbtn.click(function () {
    $("#myModal1").modal('show');
});
var $downbtn = $("#btn_down");
$downbtn.click(function () {
    $("#myModal2").modal('show');
});
$("#sava-btn").click(function () {
    var id = $("#id").val();
    var bankCode = $("#bankCode").val();
    var accName = $("#accName").val();
    var accNo = $("#accNo").val();
    var ftpHost = $("#ftpHost").val();
    var ftpPort = $("#ftpPort").val();
    var ftpUser = $("#ftpUser").val();
    var ftpPass = $("#ftpPass").val();
    var remotePath = $("#remotePath").val();
    $('#form_add').data('bootstrapValidator').validate();
    if (!$('#form_add').data('bootstrapValidator').isValid()) {
        return false;
    }
    if (id == null || id == "") {
        $.ajax({
            url: "/ftpconfig/add",
            type: "post",
            data: {
                bankCode: bankCode,
                accName: accName,
                accNo: accNo,
                ftpHost: ftpHost,
                ftpPort: ftpPort,
                ftpUser: ftpUser,
                ftpPass: ftpPass,
                remotePath: remotePath
            },
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.success(data.message);
                    $("#myModal1").modal('hide');
                    $("#form_add input").val("");
                    $("button[name=refresh]").click();
                    $('#form_add').data('bootstrapValidator').resetForm();
                } else {
                    toastr.error(data.message);
                }

            }
        })
    } else {
        $.ajax({
            url: "/ftpconfig/edit",
            type: "post",
            data: {
                id: id,
                bankCode: bankCode,
                accName: accName,
                accNo: accNo,
                ftpHost: ftpHost,
                ftpPort: ftpPort,
                ftpUser: ftpUser,
                ftpPass: ftpPass,
                remotePath: remotePath
            },
            dataType: "json",
            success: function (data) {
                if (data.code === 200) {
                    toastr.success(data.message);
                    $("#myModal1").modal('hide');
                    $("#form_add input").val("");
                    $("#id").val('');
                    $("button[name=refresh]").click();
                    $('#form_add').data('bootstrapValidator').resetForm();
                } else {
                    toastr.error(data.message);
                }

            }
        })
    }
});

function upConfig(index) {
    var info = $("#configTable").bootstrapTable('getData')[index];
    if (info.length == 0) {
        toastr.error("请选择数据");
    } else {
        $("#myModal1").modal('show');
        $("#id").val(info.id);
        $("#bankCode").val(info.bankCode);
        $("#accName").val(info.accName);
        $("#accNo").val(info.accNo);
        $("#ftpHost").val(info.ftpHost);
        $("#ftpPort").val(info.ftpPort);
        $("#ftpUser").val(info.ftpUser);
        $("#ftpPass").val(info.ftpPass);
        $("#remotePath").val(info.remotePath);
    }
}

//下载
var $downbtn = $("#down-btn");

$downbtn.click(function () {
    var tranDate = $("#tranDate").val();
    if (tranDate == null || tranDate == '' || tranDate === undefined){
        toastr.error("请选择日期！");
        return ;
    }
    $.ajax({
        url: "/isFileExists",
        type: "post",
        data: {
            tranDate: tranDate
        },
        dataType: "json",
        success: function (data) {
            $("#myModal2").modal('hide');
            $("#tranDate").val('');
            if (data.code === 200) {
                window.location.href = "/fileDownload?tranDate=" + tranDate;
            } else {
                toastr.error(data.message);
            }
        }
    })
});
