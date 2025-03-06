console.info("[Host] auth.js starts");

tj.login = function (obj) {
    tj.customCommand({
        success: function(res) {
            console.info("[Host] loginUnity success res = " + res);
            let resObj = JSON.parse(res);
            obj.success(resObj);
        },
        fail: function(res) {
            console.info("[Host] loginUnity fail res=" + res);
            let apiRes = {
                errMsg: res.errorMsg,
                errno: res.errorCode
            };
            obj.fail(apiRes);

        },
        complete: function() {
            console.info("[Host] loginUnity complete");
            obj.complete();
        }
    }, "loginUnity");
};