const bmap = require('../libs/bmap-wx.min.js')

export const BAIDU_AK_MP = 'ZmaKnx0vJcB745El7rFTFdqadqxM5JXt'
export const BAIDU_AK_BROWSER = 'Ysj2vZPKWz8nwGRiPYD7MBBOvVtQiGlt'

export const createBMap = () => {
  return new bmap.BMapWX({ ak: BAIDU_AK_MP })
}

