const validators = {
    /**
     * 可区分全角字符/半角字符的长度校验。
     * @param min
     * @param max
     * @returns {Function}
     */
    length({min=0,max=100000000}){
      return function(rule, value,callback){
        //将一个全角字符替换成两个半角字符，以得到真实长度。
        let realLength = value.replace(/[\u0391-\uFFE5]/g,'aa').length;
        realLength <= max && realLength >= min ? callback() : max<100000000 ? callback('输入长度应在'+min+'到'+max+'个字符之间！') : callback('至少应输入'+min+'个字符！');
      }
    }
  }
  const install = function(Vue, options) {
    Vue.prototype.validators = validators;
  }
  export default { install }