/*
console.info("[Host] perf.js starts");

cusObj.reportPerformanceImplement = function(obj) {
    console.info("run reportPerformanceImplement");

    obj["game_id"] = cusObj.hostUrl;
    obj["report_interval"] = cusObj.reportPerformanceInterval;

    tj.customCommand({
            success: function(res) {
                console.info("reportPerformanceImplement success");
            },
            fail: function(obj) {
                console.info("reportPerformanceImplement fail")
            },
            complete: function() {
                console.info("reportPerformanceImplement complete")
            }
    }, "uploadPerf", JSON.stringify(obj));
};
*/