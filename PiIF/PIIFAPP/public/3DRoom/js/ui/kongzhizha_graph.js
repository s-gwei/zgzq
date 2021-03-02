function getData (dm, g2d) {
  // getFSZData().then(res => {
  //   let dataInfo = res.data.rows[0]
  //   for (let x in dataInfo) {
  //     if (g2d.dm().getDataByTag(x)) {
  //       if (g2d.dm().getDataByTag(x)._displayName === 'LIGHT') {
  //         g2d.dm().getDataByTag(x).a('state', dataInfo[x])
  //       }
  //       if (g2d.dm().getDataByTag(x)._displayName === 'TEXT') {
  //         if (x === 'control_mode') {
  //           console.log(dataInfo[x])
  //           if (dataInfo[x] === '0') {
  //             g2d.dm().getDataByTag(x).s('text', '现地')
  //           } else if (dataInfo[x] === '1') {
  //             g2d.dm().getDataByTag(x).s('text', '远方')
  //           }
  //         }
  //         if (x === 'fillWater_pattern') {
  //           if (dataInfo[x] === '0') {
  //             g2d.dm().getDataByTag(x).s('text', '充水')
  //           } else if (dataInfo[x] === '1') {
  //             g2d.dm().getDataByTag(x).s('text', '常规')
  //           }
  //         }
  //         if (x === 'control_pattern') {
  //           if (dataInfo[x] === '0') {
  //             g2d.dm().getDataByTag(x).s('text', '自动')
  //           } else if (dataInfo[x] === '1') {
  //             g2d.dm().getDataByTag(x).s('text', '手动')
  //           }
  //         }
  //       }
  //     }
  //   }
  // }, function (res) {
  //
  // })

  // 获取闸门开度
  let show_open_num = g2d.dm().getDataByTag('show_open_num')
  let zm_num
  // getZMKD().then(res => {
  //   zm_num = res.data.rows[0].result
  //   show_open_num.a('ht.value', zm_num)
  // }, function (res) {
  //
  // })

  // 获取报警信息
  // getAlertInfo().then(res => {
  //   let tableInfo = g2d.dm().getDataByTag('show_alert_table')
  //   tableInfo.a('ht.dataSource', res.data.rows)
  // }, function (res) {
  //
  // })
}
// 创建调度，水流动
let show_water_flow = 0.5
let show_waterAnimation = {
  interval: 300,
  action: function (data) {
    show_water_flow -= 0.1
    let show_Water = g3d.dm().getDataByTag('show_shui1')
    if (data !== show_Water) {
      return
    }
    show_Water.s('shape3d.uv.offset', [0, show_water_flow])
  }
}

let getG3dData = {
  interval: 5000,
  action () {
    let showZM = g3d.dm().getDataByTag('show_zhamen')
    let showS = g3d.dm().getDataByTag('show_shui1')
    let showSZ1 = g3d.dm().getDataByTag('show_shengzi1')
    let showSZ2 = g3d.dm().getDataByTag('show_shengzi2')
    let show_zm_kd
    // getZMKD().then(res => {
    //   show_zm_kd = res.data.rows[0].result
    //   showS.s('3d.visible', true)
    //   let s_pos = show_zm_kd / 100 * 12
    //   showS.s3(showS.getSize3d()[0], s_pos, showS.getSize3d()[2])
    //   let show_z_kd = show_zm_kd / 100 * 145 + (-144.93484)
    //   showZM.setPosition3d([59.57058, show_z_kd, -87])
    //
    //   showSZ1.s3(showSZ1.getSize3d()[0], 3.2884 + (1.5 - show_zm_kd / 100 * 1.5), showSZ1.getSize3d()[2])
    //   showSZ2.s3(showSZ2.getSize3d()[0], 3.2884 + (1.5 - show_zm_kd / 100 * 1.5), showSZ2.getSize3d()[2])
    // }, function (res) {
    //
    // })
  }
}

let dataModel = window.dataModel = new ht.DataModel();
let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

g2d.setMovableFunc(() => false);

g3d.addToDOM();
g2d.addToDOM(g3d.getView());
g3d.setMovableFunc(() => false);
g3d.getBrightness = () => 1;

// 选中边框为0
g2d.getSelectWidth = function () {
  return 0;
};
// 禁止鼠标缩放
g2d.handleScroll = function () {
};
// 禁止 touch 下双指缩放
g2d.handlePinch = function () {
};
// 禁止平移
g2d.setPannable(false);
// 禁止框选
// g2d.setRectSelectable(false);
// 隐藏滚动条
g2d.setScrollBarVisible(false);


function getWheelEventName(element){
  if ('onwheel' in element) {
    // 标准事件
    return 'wheel';
  } else if (document.onmousewheel !== undefined) {
    // 通用旧版事件
    return 'mousewheel';
  } else {
    // 旧版 Firefox 事件
    return 'DOMMouseScroll';
  }
}
function interaction2D3D (gv) {
  let div2d = gv.getView()
  let handler = event => {
    if (gv.getDataAt(event)) {
      event.preventDefault()
      event.stopPropagation()
    }
  }
  div2d.addEventListener('mousedown', handler)
  div2d.addEventListener('touchstart', handler)
  div2d.addEventListener(getWheelEventName(div2d) , handler)
}
// 避免23d交互重叠
interaction2D3D(g2d);
//
// // 避免23d交互重叠
// this.event = new ht.Notifier();

g3d.dm().clear()
g3d.deserialize('scenes/hjx/lianjiedong/zhamen.json', (json, dm, gv, datas) => {
  g3d.setEye([84.88297399211088, 266.8122737037639, 1210.6553206714646])
  g3d.setCenter([209.7142869612094, -46.602940365675636, -282.0007327784762])

  g3d.dm().addScheduleTask(show_waterAnimation)// 添加水流动动画调度
  g3d.dm().addScheduleTask(getG3dData)// 添加水流动动画调度
})

g2d.dm().clear()
g2d.deserialize('displays/hjx/lianjiedong/kongzhizha_graph.json', (json, dm, g2d, datas) => {

})

g2d.mi(e => {
  const kind = e.kind
  if (kind === 'clickData') {
  }
});

g3d.mi(e => {
  const kind = e.kind
  if (kind === 'clickData') {

  }
});
