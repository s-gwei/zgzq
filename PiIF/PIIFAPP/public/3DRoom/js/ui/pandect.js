//创建调度，扫煤旋转
let mapweizhi = {
  interval: 70,
  action: function (data) {
    let dingwei = g3d.dm().getDataByTag('dingwei')
    if (data !== dingwei) {
      return
    }
    dingwei.setRotationY(dingwei.getRotationY() + Math.PI / 3)
  }
}
let mapweizhi1 = {
  interval: 70,
  action: function (data) {
    let dingwei1 = g3d.dm().getDataByTag('dingwei1')
    if (data !== dingwei1) {
      return
    }

    dingwei1.setRotationY(dingwei1.getRotationY() + Math.PI / 3)
  }
}

  let dataModel = window.dataModel = new ht.DataModel();
  let g2d = window.graph = new ht.graph.GraphView(dataModel);//定义2d图纸
  let g3d = window.g3d = new ht.graph3d.Graph3dView();//定义3d模型

  g2d.setMovableFunc(() => false);

  g3d.addToDOM();
  g2d.addToDOM(g3d.getView());

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
  g2d.deserialize('displays/hjx/lianjiedong/ditu_main.json',(json,dm,g3d,datas) =>{
    // 反序列化图纸完成回调
  });//反序列化图纸


  g3d.deserialize('scenes/hjx/ditu/ditu.json',(json,dm,g3d,datas) =>{
    g3d.setEye([1525.2217456678814, 470.23129944294783, 156.0990138844033])
    g3d.setCenter([-46.098168831861834, 124.68718233594555, 115.97585049116111])
    g3d.dm().addScheduleTask(mapweizhi)// 添加调度
    g3d.dm().addScheduleTask(mapweizhi1)// 添加调度
  });//反序列化3d

  g2d.mi(e => {
    const kind = e.kind
    if (kind === 'clickData') {
      // 远程上升
      if (e.data.getTag() === 'tm_Btn') {

        g3d.dm().removeScheduleTask(mapweizhi)
        g3d.dm().removeScheduleTask(mapweizhi1)
        let datas = this.g3d.dm()._datas
        for (let i = 0; i < datas.length; i++) {
          if (datas._as[i]._displayName === 'moxing') {
            datas._as[i].s({
              '3d.visible': false
            })
          }
          if (datas._as[i]._displayName === 'd_moxing') {
            datas._as[i].s({
              '3d.visible': true
            })
          }

        }
      }

      if (e.data.getTag() === 'back_btn') {
        g3d.dm().addScheduleTask(mapweizhi)// 添加调度
        g3d.dm().addScheduleTask(mapweizhi1)// 添加调度
        let datas = this.g3d.dm()._datas
        for (let i = 0; i < datas.length; i++) {
          if (datas._as[i]._displayName === 'd_moxing') {
            datas._as[i].s({
              '3d.visible': false
            })
          }
          if (datas._as[i]._displayName === 'moxing') {
            datas._as[i].s({
              '3d.visible': true
            })
          }
        }
      }
    }
  });
  //双击查看
  g3d.mi(e => {
    const kind = e.kind
    if (kind === 'clickData') {
      if ((e.data.getTag() === 'map_shk') || (e.data.getTag() === 's_map_shk')) {
        // this.main.$router.push({
        //   name: 'shk_Main'
        // })
        window.parent.postMessage('/shk/main', '*');
        // parent.location.href='/report/ArchivesStatisticst';
      }
      if ((e.data.getTag() === 'map_hjx') || (e.data.getTag() === 's_map_hjx')) {
        // this.main.$router.push({
        //   name: 'hjx_main'
        // })
        window.parent.postMessage('/hjx/main', '*');
      }
    }
  });
