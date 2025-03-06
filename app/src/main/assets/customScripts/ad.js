let rewardedVideoAdOnLoadCallbacks = [];
let rewardedVideoAdOnErrorCallbacks = [];
let rewardedVideoAdOnCloseCallbacks = [];

console.log("[Host] ad.js starts");

tj.createRewardedVideoAd = (obj) => {
    console.info('[Host] invoke tj.createRewardedVideoAd');

    return {
        style: {
            width: 100,
        },
        onLoad: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.onLoad');
            if (callback && typeof callback === 'function') {
                if (!rewardedVideoAdOnLoadCallbacks.includes(callback)) {
                    rewardedVideoAdOnLoadCallbacks.push(callback);
                }
            } else {
                console.warn('[Host] RewardedVideoAd.onLoad callback is not a function or is null/undefined');
            }
        },
        onError: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.onError');
            if (callback && typeof callback === 'function') {
                if (!rewardedVideoAdOnErrorCallbacks.includes(callback)) {
                    rewardedVideoAdOnErrorCallbacks.push(callback);
                }
            } else {
                console.warn('[Host] RewardedVideoAd.onError callback is not a function or is null/undefined');
            }
        },
        onClose: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.onClose');
            if (callback && typeof callback === 'function') {
                if (!rewardedVideoAdOnCloseCallbacks.includes(callback)) {
                    rewardedVideoAdOnCloseCallbacks.push(callback);
                }
            } else {
                console.warn('[Host] RewardedVideoAd.onClose callback is not a function or is null/undefined');
            }
        },
        load: () => {
            console.info('[Host] invoke RewardedVideoAd.load');

            tj.customCommand({
                success: function(res) {
                    console.info("[Host] RewardedVideoAd.load success");
                },
                fail: function(obj) {
                    console.info("[Host] RewardedVideoAd.load fail");
                },
                complete: function() {
                    console.info("[Host] RewardedVideoAd.load complete");
                }
            }, "loadRewardAd");

            return Promise.resolve();
        },
        show: () => {
            console.info('[Host] invoke RewardedVideoAd.show');

            tj.customCommand({
                success: function(res) {
                    console.info("[Host] RewardedVideoAd.show success");
                },
                fail: function(obj) {
                    console.info("[Host] RewardedVideoAd.show fail");
                },
                complete: function() {
                    console.info("[Host] RewardedVideoAd.show complete");
                }
            }, "showRewardAd");

            return Promise.resolve();
        },
        offLoad: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.offLoad');
            if (callback === undefined) rewardedVideoAdOnLoadCallbacks = [];
            else if (callback && typeof callback === 'function') {
                rewardedVideoAdOnLoadCallbacks = rewardedVideoAdOnLoadCallbacks.filter((cb) => cb !== callback);
            } else {
                console.warn('[Host] RewardedVideoAd.offLoad callback is not a function or is null');
            }
        },
        offError: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.offError');
            if (callback === undefined) rewardedVideoAdOnErrorCallbacks = [];
            else if (callback && typeof callback === 'function') {
                rewardedVideoAdOnErrorCallbacks = rewardedVideoAdOnErrorCallbacks.filter((cb) => cb !== callback);
            } else {
                console.warn('[Host] RewardedVideoAd.offError callback is not a function or is null');
            }
        },
        offClose: (callback) => {
            console.info('[Host] invoke RewardedVideoAd.offClose');
            if (callback === undefined) rewardedVideoAdOnCloseCallbacks = [];
            else if (callback && typeof callback === 'function') {
                rewardedVideoAdOnCloseCallbacks = rewardedVideoAdOnCloseCallbacks.filter((cb) => cb !== callback);
            } else {
                console.warn('[Host] RewardedVideoAd.offClose callback is not a function or is null');
            }
        },
        destroy: () => {
            console.info('[Host] invoke RewardedVideoAd.destroy');
            rewardedVideoAdOnLoadCallbacks = [];
            rewardedVideoAdOnErrorCallbacks = [];
            rewardedVideoAdOnCloseCallbacks = [];
        },
    };
};

rewardedVideoCloseCallbackFromJava = (isEnded) => {
    try {
        console.info(`[Host] invoke rewardedVideoCloseCallbackFromJava ${isEnded}`);
        rewardedVideoAdOnCloseCallbacks.forEach((callback) => callback({ isEnded: isEnded }));
    } catch (error) {
        console.error('[Host] Error in rewardedVideoCloseCallbackFromJava:', error);
    }
};

rewardedVideoLoadCallbackFromJava = () => {
    try {
        console.info("[Host] invoke rewardedVideoLoadCallbackFromJava");
        rewardedVideoAdOnLoadCallbacks.forEach((callback) => callback());
    } catch (error) {
        console.error('[Host] Error in rewardedVideoLoadCallbackFromJava:', error);
    }
};

rewardedVideoErrorCallbackFromJava = (errMsg, errCode) => {
    try {
        console.info("[Host] invoke rewardedVideoErrorCallbackFromJava");
        rewardedVideoAdOnErrorCallbacks.forEach((callback) => callback({ errMsg: errMsg, errCode: errCode }));
    } catch (error) {
        console.error('[Host] Error in rewardedVideoErrorCallbackFromJava', error);
    }
};