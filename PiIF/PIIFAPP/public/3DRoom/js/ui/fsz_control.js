// 创建调度，闸门上升
let zhamenUp = {
  interval: 150,
  action: function (data) {
    let zhamen_up = g3d.dm().getDataByTag('zhamen')
    let open_num = g2d.dm().getDataByTag('open_num')
    let circle_kd = g2d.dm().getDataByTag('circle_kd')
    let zh = g2d.dm().getDataByTag('zh_sw')
    let gv_zh = g2d.dm().getDataByTag('gv_sh_sw')
    let zh_t = zh.s('text')
    let zh_n = Number(zh_t)

    if (data !== zhamen_up) {
      return
    }
    if (zhamen_up.getPosition3d()[1] > 0) {
      return
    }
    zhamen_up.setPosition3d([59.57058, zhamen_up.getPosition3d()[1] + 1, -87])
    zh.s('text', (zh_n + 0.03).toFixed(2))
    gv_zh.s('text', (zh_n + 0.03).toFixed(2))
    let z_kd = zhamen_up.getPosition3d()[1] - (-144.93484)
    let z_kd_p = (z_kd / 145) * 100
    let openValue = '1'
    if (z_kd_p === 100) {
      // setZMOpen(openValue).then(res => {
      //   getControlData(g2d.dm(), g2d)
      // }, function (res) {
      //
      // })
    }
    open_num.a('ht.value', z_kd_p)
    circle_kd.a('progressPercentage', Math.round(z_kd_p) / 100)
    // setZMKD(z_kd_p).then(res => {
    //
    // }, function (res) {
    //
    // })
  }
}

// 创建调度，绳子1变短
let sz1_minus = {
  interval: 150,
  action: function (data) {
    let shengzi1 = g3d.dm().getDataByTag('shengzi1')
    if (data !== shengzi1) {
      return
    }
    if (shengzi1.getSize3d()[1] < 3.2) {
      return
    }
    shengzi1.s3(shengzi1.getSize3d()[0], shengzi1.getSize3d()[1] - 0.011, shengzi1.getSize3d()[2])
  }
}

// 创建调度，绳子2变短
let sz2_minus = {
  interval: 150,
  action: function (data) {
    let shengzi2 = g3d.dm().getDataByTag('shengzi2')
    if (data !== shengzi2) {
      return
    }
    if (shengzi2.getSize3d()[1] < 3.2) {
      return
    }
    shengzi2.s3(shengzi2.getSize3d()[0], shengzi2.getSize3d()[1] - 0.011, shengzi2.getSize3d()[2])
  }
}

// 闸室水动画，水位上升
let zs_shui_dh = {
  interval: 150,
  action: function (data) {
    let zhashi_shui = g3d.dm().getDataByTag('shui1')
    if (data !== zhashi_shui) {
      return
    }
    // zhashi_shui.setY(zhashi_shui.getY()+1)
    if (g3d.dm().getDataByTag('zhamen').getPosition3d()[1] > 0) {
      return
    }
    if (zhashi_shui.getSize3d()[1] > 11.5) {
      return
    }
    zhashi_shui.s3(zhashi_shui.getSize3d()[0], zhashi_shui.getSize3d()[1] + 0.095, zhashi_shui.getSize3d()[2])
  }
}

// 创建调度，闸门下降
let zhamenDown = {
  interval: 150,
  action: function (data) {
    let zhamen_down = g3d.dm().getDataByTag('zhamen')
    let open_num = g2d.dm().getDataByTag('open_num')
    let circle_kd = g2d.dm().getDataByTag('circle_kd')
    let zh = g2d.dm().getDataByTag('zh_sw')
    let gv_zh = g2d.dm().getDataByTag('gv_sh_sw')
    let zh_t = zh.s('text')
    let zh_n = Number(zh_t)

    if (data !== zhamen_down) {
      return
    }
    if (zhamen_down.getPosition3d()[1] < -144) {
      return
    }
    zhamen_down.setPosition3d([zhamen_down.getPosition3d()[0], zhamen_down.getPosition3d()[1] - 1, zhamen_down.getPosition3d()[2]])
    zh.s('text', (zh_n - 0.03).toFixed(2))
    gv_zh.s('text', (zh_n - 0.03).toFixed(2))
    let z_kd = zhamen_down.getPosition3d()[1] - (-144.93484)
    let z_kd_p = (z_kd / 145) * 100
    let closeValue = '1'
    if (z_kd_p === 0) {
      // setZMClose(closeValue).then(res => {
      //   getControlData(g2d.dm(), g2d)
      // }, function (res) {
      //
      // })
    }
    open_num.a('ht.value', z_kd_p)
    circle_kd.a('progressPercentage', Math.round(z_kd_p) / 100)
    // setZMKD(z_kd_p).then(res => {
    //
    // }, function (res) {
    //
    // })
  }
}

// 创建调度，绳子1变长
let sz1_plus = {
  interval: 150,
  action: function (data) {
    let sz_plus1 = g3d.dm().getDataByTag('shengzi1')
    if (data !== sz_plus1) {
      return
    }

    if (sz_plus1.getSize3d()[1] >= 4.7884) {
      return
    }
    sz_plus1.s3(sz_plus1.getSize3d()[0], sz_plus1.getSize3d()[1] + 0.011, sz_plus1.getSize3d()[2])
  }
}

// 创建调度，绳子2变长
let sz2_plus = {
  interval: 150,
  action: function (data) {
    let sz2_plus = g3d.dm().getDataByTag('shengzi2')
    if (data !== sz2_plus) {
      return
    }
    if (sz2_plus.getSize3d()[1] >= 4.7884) {
      return
    }
    sz2_plus.s3(sz2_plus.getSize3d()[0], sz2_plus.getSize3d()[1] + 0.011, sz2_plus.getSize3d()[2])
  }
}

// 创建调度，水流动
let water_flow = 0.5
let water_animation = {
  interval: 300,
  action: function (data) {
    water_flow -= 0.1
    let flowWater = g3d.dm().getDataByTag('shui1')
    if (data !== flowWater) {
      return
    }
    flowWater.s('shape3d.uv.offset', [0, water_flow])
  }
}

// 创建调度，水位下降
let zs_shui_minus = {
  interval: 150,
  action: function (data) {
    let zhashi_shui = g3d.dm().getDataByTag('shui1')
    if (data !== zhashi_shui) {
      return
    }
    if (g3d.dm().getDataByTag('zhamen').getPosition3d()[1] < -144) {
      g3d.dm().getDataByTag('shui1').s('3d.visible', false)
      return
    }
    zhashi_shui.s3(zhashi_shui.getSize3d()[0], zhashi_shui.getSize3d()[1] - 0.085, zhashi_shui.getSize3d()[2])
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
  g3d.deserialize('scenes/hjx/lianjiedong/fsz_zm_control.json', (json, dm, gv, datas) => {
    g3d.setEye([84.88297399211088, 266.8122737037639, 1210.6553206714646])
    g3d.setCenter([209.7142869612094, -46.602940365675636, -282.0007327784762])
  })

  g2d.dm().clear()
  g2d.deserialize('displays/hjx/lianjiedong/fsz_control.json', (json, dm, g2d, datas) => {
    // g2d.dm().addScheduleTask(this.main.get2dData)// 添加闸门上升调度
    // getControlData(dm, g2d)
    // this.main.get2dData = setInterval(function () {
    //   getControlData(dm, g2d)
    // }, 5000)
  })

  g2d.mi(e => {
    const kind = e.kind
    if (kind === 'clickData') {
      if (e.data.getTag() === 'btn_up') {
        g3d.dm().addScheduleTask(zhamenUp)// 添加闸门上升调度
        g3d.dm().getDataByTag('shui1').s('3d.visible', true)
        g3d.dm().removeScheduleTask(zs_shui_minus)// 添加水位下降调度
        g3d.dm().addScheduleTask(water_animation)// 添加水流动调度
        g3d.dm().addScheduleTask(zs_shui_dh)// 添加水位上升调度
        g3d.dm().removeScheduleTask(sz1_plus)// 删除绳子1变长调度
        g3d.dm().removeScheduleTask(sz2_plus)// 删除绳子2变长调度
        g3d.dm().addScheduleTask(sz1_minus)// 添加绳子1变短调度
        g3d.dm().addScheduleTask(sz2_minus)// 添加绳子2变短调度
        g3d.dm().removeScheduleTask(zhamenDown)

        let upValue = '1'
        let dm = g2d.dm()
        // setZMUp(upValue).then(res => {
        //   getControlData(dm, g2d)
        // }, function (res) {
        //
        // })
      }
      if (e.data.getTag() === 'btn_down') {
        let downValue = '1'
        let dm = g2d.dm()
        // setZMDown(downValue).then(res => {
        //   getControlData(dm, g2d)
        // }, function (res) {
        //
        // })
        g3d.dm().removeScheduleTask(zhamenUp)// 删除闸门上升调度
        g3d.dm().addScheduleTask(zhamenDown)// 添加闸门下降调度
        g3d.dm().addScheduleTask(water_animation)// 添加水流动动画
        g3d.dm().removeScheduleTask(zs_shui_dh)// 删除水位上升调度
        g3d.dm().addScheduleTask(zs_shui_minus)// 添加水位下降调度
        g3d.dm().removeScheduleTask(sz1_minus)// 删除绳子1变短调度
        g3d.dm().removeScheduleTask(sz2_minus)// 删除绳子2变短调度
        g3d.dm().addScheduleTask(sz1_plus)// 添加绳子1变长调度
        g3d.dm().addScheduleTask(sz2_plus)// 添加绳子2变长调度
      }
      if (e.data.getTag() === 'btn_stop') {
        let stopValue = '1'
        let dm = g2d.dm()
        // setZMStop(stopValue).then(res => {
        //   getControlData(dm, g2d)
        // }, function (res) {
        //
        // })

        // 设置闸门开度
        let z_kd = g3d.dm().getDataByTag('zhamen').getPosition3d()[1] - (-144.93484)
        let z_kd_p = (z_kd / 145) * 100
        // setZMKD(z_kd_p).then(res => {
        //
        // }, function (res) {
        //
        // })
        g3d.dm().removeScheduleTask(zhamenUp)// 删除调度
        g3d.dm().removeScheduleTask(zhamenDown)// 删除调度
        g3d.dm().removeScheduleTask(zs_shui_dh)// 删除水位上升调度
        g3d.dm().removeScheduleTask(zs_shui_minus)// 删除水位下降调度
        g3d.dm().removeScheduleTask(sz1_minus)// 删除绳子1变短调度
        g3d.dm().removeScheduleTask(sz2_minus)// 删除绳子2变短调度
        g3d.dm().removeScheduleTask(sz1_plus)// 删除绳子1变长调度
        g3d.dm().removeScheduleTask(sz2_plus)// 删除绳子2变长调度
      }
      if (e.data.getTag() === 'close_win') {
        g2d.dm().getDataByTag('win_vedio').s('2d.visible', false)
      }

      // if (e.data.getTag() === 'control_mode') {
      //   getFSZData().then(res => {
      //     let dataInfo = res.data.rows[0]
      //     let control_mode_value
      //     if (dataInfo['control_mode'] === '0') {
      //       control_mode_value = '1'
      //     } else if (dataInfo['control_mode'] === '1') {
      //       control_mode_value = '0'
      //     }
      //     setControlMode(control_mode_value).then(res => {
      //       getControlData(this.g2d.dm(), this.g2d)
      //     }, function (res) {
      //
      //     })
      //   }, function (res) {
      //   })
      // }
      // if (e.data.getTag() === 'control_pattern') {
      //   getFSZData().then(res => {
      //     let dataInfo = res.data.rows[0]
      //     let control_pattern_value
      //     if (dataInfo['control_pattern'] === '0') {
      //       control_pattern_value = '1'
      //     } else if (dataInfo['control_pattern'] === '1') {
      //       control_pattern_value = '0'
      //     }
      //     setControlPattern(control_pattern_value).then(res => {
      //       getControlData(this.g2d.dm(), this.g2d)
      //     }, function (res) {
      //
      //     })
      //   }, function (res) {
      //
      //   })
      // }
      // if (e.data.getTag() === 'fillWater_pattern') {
      //   getFSZData().then(res => {
      //     let dataInfo = res.data.rows[0]
      //     let fillWater_pattern_value
      //     if (dataInfo['fillWater_pattern'] === '0') {
      //       fillWater_pattern_value = '1'
      //     } else if (dataInfo['fillWater_pattern'] === '1') {
      //       fillWater_pattern_value = '0'
      //     }
      //
      //     setFillWater(fillWater_pattern_value).then(res => {
      //       getControlData(this.g2d.dm(), this.g2d)
      //     }, function (res) {
      //
      //     })
      //   }, function (res) {
      //
      //   })
      // }
      if (e.data.getTag() === 'close_deviceInfo') {
        g2d.dm().getDataByTag('deviceInfo').s('2d.visible', false)
      }
    }
  });

  //双击查看
  g3d.mi(e => {
    const kind = e.kind
    if (kind === 'clickData') {
      if (e.data.getTag() === 'vedio_camera') {
        g2d.dm().getDataByTag('win_vedio').s('2d.visible', true)
      }
      if (e.data.getTag() === 'zhashi') {
        g2d.dm().getDataByTag('deviceInfo').s('2d.visible', true)
      }
    }
  });
