// This file is used to simulate the game developer calling the js interface provided by the host

// Reward Ad, referring https://developers.weixin.qq.com/miniprogram/dev/api/ad/RewardedVideoAd.onError.html
console.log("[Mock Game] mock_ad.js starts");

rewardedVideoAd = tj.createRewardedVideoAd(null); // this should be a object which has field adUnitId

rewardedVideoAd.onLoad(() => {
    console.log("[Mock Game] Rewarded Ad is Loaded");

    rewardedVideoAd.show(); // show immediately after loading successfully
});

rewardedVideoAd.onError((errMsg, errCode) => {
    console.log("[Mock Game] Rewarded Ad errorMsg: ${errMsg}, errorCode: ${errCode}");
});

rewardedVideoAd.onClose((isEnded) => {
    if (isEnded) {
        console.log("[Mock Game] Rewarded Ad is closed, we should send rewards")
    } else {
        console.log("[Mock Game] Rewarded Ad is closed, no rewards")
    }

    rewardedVideoAd.destroy(); // destroy it after closing
});

rewardedVideoAd.load();