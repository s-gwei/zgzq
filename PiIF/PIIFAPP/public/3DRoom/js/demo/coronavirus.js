var g = function() {
    "use strict";
    function t(t) {
        return function() {
            var o = t.apply(this, arguments);
            return new Promise(function(r, s) {
                return function e(t, a) {
                    try {
                        var i = o[t](a),
                            n = i.value
                    } catch(t) {
                        return void s(t)
                    }
                    if (!i.done) return Promise.resolve(n).then(function(t) {
                            e("next", t)
                        },
                        function(t) {
                            e("throw", t)
                        });
                    r(n)
                } ("next")
            })
        }
    }
    function a(t, e) {
        if (! (t instanceof e)) throw new TypeError("Cannot call a class as a function")
    }
    var r = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ?
        function(t) {
            return typeof t
        }: function(t) {
            return t && "function" == typeof Symbol && t.constructor === Symbol && t !== Symbol.prototype ? "symbol": typeof t
        },
        e = function(t, e, a) {
            return e && i(t.prototype, e),
            a && i(t, a),
                t
        };
    function i(t, e) {
        for (var a = 0; a < e.length; a++) {
            var i = e[a];
            i.enumerable = i.enumerable || !1,
                i.configurable = !0,
            "value" in i && (i.writable = !0),
                Object.defineProperty(t, i.key, i)
        }
    }
    function n(t, e) {
        if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function, not " + typeof e);
        t.prototype = Object.create(e && e.prototype, {
            constructor: {
                value: t,
                enumerable: !1,
                writable: !0,
                configurable: !0
            }
        }),
        e && (Object.setPrototypeOf ? Object.setPrototypeOf(t, e) : t.__proto__ = e)
    }
    function s(t, e) {
        if (!t) throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
        return ! e || "object" != typeof e && "function" != typeof e ? t: e
    }
    function o() {
        a(this, o),
            this._eventMap = {}
    }
    var c, u = new(e(o, [{
            key: "add",
            value: function(t, e, a, i) {
                var n = 3 < arguments.length && void 0 !== i && i,
                    r = this._eventMap[t]; (r = r || (this._eventMap[t] = new ht.Notifier)).add(e, a, n)
            }
        },
            {
                key: "remove",
                value: function(t, e, a) {
                    var i = this._eventMap[t];
                    i && i.remove(e, a)
                }
            },
            {
                key: "fire",
                value: function(t, e) {
                    var a = this._eventMap[t];
                    a && a.fire(e)
                }
            }]), o),
        h = "switchView",
        d = ["湖北", "广东", "浙江", "上海", "北京", "天津", "重庆", "福建", "甘肃", "海南", "河北", "黑龙江", "河南", "湖南", "江苏", "江西", "吉林", "辽宁", "宁夏", "陕西", "山东", "安徽", "山西", "四川", "台湾", "青海", "云南", "贵州", "新疆", "内蒙古", "西藏", "广西", "澳门", "香港"],
        l = [65, 425, 1054],
        f = [ - 11, -24, -3],
        p = [47.14053656250616, 328.13310001413527, 371.85881054006524],
        v = [ - 20.51089145864748, -35.16296227576809, 28.64961126300672],
        m = "rgb(237,241,255)",
        y = 370 / 18 + 1,
        g = Math.PI,
        b = Math.pow,
        D = Math.sin,
        w = {
            swing: function(t) {
                return - Math.cos(t * g) / 2 + .5
            },
            easeNone: function(t) {
                return t
            },
            easeIn: function(t) {
                return t * t
            },
            easeOut: function(t) {
                return (2 - t) * t
            },
            easeBoth: function(t) {
                return (t *= 2) < 1 ? .5 * t * t: .5 * (1 - --t * (t - 2))
            },
            easeInStrong: function(t) {
                return t * t * t * t
            },
            easeOutStrong: function(t) {
                return 1 - --t * t * t * t
            },
            easeBothStrong: function(t) {
                return (t *= 2) < 1 ? .5 * t * t * t * t: .5 * (2 - (t -= 2) * t * t * t)
            },
            elasticIn: function(t) {
                return 0 === t || 1 === t ? t: -(b(2, 10 * --t) * D(2 * g * (t - .075) / .3))
            },
            elasticOut: function(t) {
                return 0 === t || 1 === t ? t: b(2, -10 * t) * D(2 * g * (t - .075) / .3) + 1
            },
            elasticBoth: function(t) {
                return 0 === t || 1 === t ? t: t < 1 ? b(2, 10 * --t) * D(2 * g * (t - .1125) / .45) * -.5 : b(2, -10 * --t) * D(2 * g * (t - .1125) / .45) * .5 + 1
            },
            backIn: function(t) {
                if (1 === t) return t;
                return t * t * (2.70158 * t - 1.70158)
            },
            backOut: function(t) {
                return--t * t * (2.70158 * t + 1.70158) + 1
            },
            backBoth: function(t) {
                var e = 1.70158;
                return (t *= 2) < 1 ? t * t * ((1 + (e *= 1.525)) * t - e) * .5 : .5 * ((t -= 2) * t * ((1 + (e *= 1.525)) * t + e) + 2)
            },
            bounceIn: function(t) {
                return 1 - w.bounceOut(1 - t)
            },
            bounceOut: function(t) {
                var e = 7.5625;
                return t < 1 / 2.75 ? e * t * t: t < 2 / 2.75 ? e * (t -= 1.5 / 2.75) * t + .75 : t < 2.5 / 2.75 ? e * (t -= 2.25 / 2.75) * t + .9375 : e * (t -= 2.625 / 2.75) * t + .984375
            },
            bounceBoth: function(t) {
                return t < .5 ? .5 * w.bounceIn(2 * t) : .5 * w.bounceOut(2 * t - 1) + .5
            }
        },
        T = (c = {},
            function(t, n, r, e) {
                var s = !e,
                    o = void 0;
                s && (o = c[n]),
                    t.deserialize(o || n,
                        function(t, e, a, i) {
                            o || (c[n] = t),
                            ht.Default.isFunction(r) && r(t, e, a, i, s)
                        })
            });
    function S(t) {
        var e = new Date;
        e.setDate(e.getDate() + t);
        var a = e.getFullYear(),
            i = e.getMonth() + 1 < 10 ? "0" + (e.getMonth() + 1) : e.getMonth() + 1,
            n = e.getDate() < 10 ? "0" + e.getDate() : e.getDate();
        return {
            short: i + "." + n,
            full: a + i + n
        }
    }
    var k = (e(x, [{
        key: "handleSwitch",
        value: function(t) {
            t === this.key ? (this.active || (this.active = !0, this.onSetup()), this.dm.clear(), this.url ? T(this.view, this.url, this.onPostDeserialize.bind(this)) : this.onPostDeserialize()) : this.active && (this.onTearDown(), this.active = !1)
        }
    },
        {
            key: "onSetup",
            value: function() {
                this.view.mi(this.handleInteractive, this),
                    this.view.mp(this.handlePropertyChange, this)
            }
        },
        {
            key: "onTearDown",
            value: function() {
                this.view.umi(this.handleInteractive, this),
                    this.view.ump(this.handlePropertyChange, this)
            }
        },
        {
            key: "onPostDeserialize",
            value: function() {}
        },
        {
            key: "handleInteractive",
            value: function() {}
        },
        {
            key: "handlePropertyChange",
            value: function() {}
        }]), x);
    function x(t) {
        var e = this;
        a(this, x),
            this.url = "",
            this.key = "",
            this.active = !1,
            this.view = t,
            this.dm = t.dm(),
            u.add(h,
                function(t) {
                    e.handleSwitch(t)
                })
    }
    function L(a, i) {
        return function() {
            for (var t = new Array(arguments.length), e = 0; e < t.length; e++) t[e] = arguments[e];
            return a.apply(i, t)
        }
    }
    var E = Object.prototype.toString;
    function C(t) {
        return "[object Array]" === E.call(t)
    }
    function A(t) {
        return void 0 === t
    }
    function R(t) {
        return null !== t && "object" === (void 0 === t ? "undefined": r(t))
    }
    function B(t) {
        return "[object Function]" === E.call(t)
    }
    function P(t, e) {
        if (null != t) if ("object" !== (void 0 === t ? "undefined": r(t)) && (t = [t]), C(t)) for (var a = 0,
                                                                                                        i = t.length; a < i; a++) e.call(null, t[a], a, t);
        else for (var n in t) Object.prototype.hasOwnProperty.call(t, n) && e.call(null, t[n], n, t)
    }
    var O = {
        isArray: C,
        isArrayBuffer: function(t) {
            return "[object ArrayBuffer]" === E.call(t)
        },
        isBuffer: function(t) {
            return null !== t && !A(t) && null !== t.constructor && !A(t.constructor) && "function" == typeof t.constructor.isBuffer && t.constructor.isBuffer(t)
        },
        isFormData: function(t) {
            return "undefined" != typeof FormData && t instanceof FormData
        },
        isArrayBufferView: function(t) {
            return "undefined" != typeof ArrayBuffer && ArrayBuffer.isView ? ArrayBuffer.isView(t) : t && t.buffer && t.buffer instanceof ArrayBuffer
        },
        isString: function(t) {
            return "string" == typeof t
        },
        isNumber: function(t) {
            return "number" == typeof t
        },
        isObject: R,
        isUndefined: A,
        isDate: function(t) {
            return "[object Date]" === E.call(t)
        },
        isFile: function(t) {
            return "[object File]" === E.call(t)
        },
        isBlob: function(t) {
            return "[object Blob]" === E.call(t)
        },
        isFunction: B,
        isStream: function(t) {
            return R(t) && B(t.pipe)
        },
        isURLSearchParams: function(t) {
            return "undefined" != typeof URLSearchParams && t instanceof URLSearchParams
        },
        isStandardBrowserEnv: function() {
            return ("undefined" == typeof navigator || "ReactNative" !== navigator.product && "NativeScript" !== navigator.product && "NS" !== navigator.product) && ("undefined" != typeof window && "undefined" != typeof document)
        },
        forEach: P,
        merge: function a() {
            var i = {};
            function t(t, e) {
                "object" === r(i[e]) && "object" === (void 0 === t ? "undefined": r(t)) ? i[e] = a(i[e], t) : i[e] = t
            }
            for (var e = 0,
                     n = arguments.length; e < n; e++) P(arguments[e], t);
            return i
        },
        deepMerge: function a() {
            var i = {};
            function t(t, e) {
                "object" === r(i[e]) && "object" === (void 0 === t ? "undefined": r(t)) ? i[e] = a(i[e], t) : "object" === (void 0 === t ? "undefined": r(t)) ? i[e] = a({},
                    t) : i[e] = t
            }
            for (var e = 0,
                     n = arguments.length; e < n; e++) P(arguments[e], t);
            return i
        },
        extend: function(a, t, i) {
            return P(t,
                function(t, e) {
                    a[e] = i && "function" == typeof t ? L(t, i) : t
                }),
                a
        },
        trim: function(t) {
            return t.replace(/^\s*/, "").replace(/\s*$/, "")
        }
    };
    function j(t) {
        return encodeURIComponent(t).replace(/%40/gi, "@").replace(/%3A/gi, ":").replace(/%24/g, "$").replace(/%2C/gi, ",").replace(/%20/g, "+").replace(/%5B/gi, "[").replace(/%5D/gi, "]")
    }
    function N(t, e, a) {
        if (!e) return t;
        var i;
        if (a) i = a(e);
        else if (O.isURLSearchParams(e)) i = e.toString();
        else {
            var n = [];
            O.forEach(e,
                function(t, e) {
                    null != t && (O.isArray(t) ? e += "[]": t = [t], O.forEach(t,
                        function(t) {
                            O.isDate(t) ? t = t.toISOString() : O.isObject(t) && (t = JSON.stringify(t)),
                                n.push(j(e) + "=" + j(t))
                        }))
                }),
                i = n.join("&")
        }
        if (i) {
            var r = t.indexOf("#"); - 1 !== r && (t = t.slice(0, r)),
                t += ( - 1 === t.indexOf("?") ? "?": "&") + i
        }
        return t
    }
    function M() {
        this.handlers = []
    }
    M.prototype.use = function(t, e) {
        return this.handlers.push({
            fulfilled: t,
            rejected: e
        }),
        this.handlers.length - 1
    },
        M.prototype.eject = function(t) {
            this.handlers[t] && (this.handlers[t] = null)
        },
        M.prototype.forEach = function(e) {
            O.forEach(this.handlers,
                function(t) {
                    null !== t && e(t)
                })
        };
    function q(e, a, t) {
        return O.forEach(t,
            function(t) {
                e = t(e, a)
            }),
            e
    }
    function H(t) {
        return ! (!t || !t.__CANCEL__)
    }
    function I(a, i) {
        O.forEach(a,
            function(t, e) {
                e !== i && e.toUpperCase() === i.toUpperCase() && (a[i] = t, delete a[e])
            })
    }
    function _(t, e, a, i, n) {
        var r, s, o, c, u, h = new Error(t);
        return s = e,
            o = a,
            c = i,
            u = n,
            (r = h).config = s,
        o && (r.code = o),
            r.request = c,
            r.response = u,
            r.isAxiosError = !0,
            r.toJSON = function() {
                return {
                    message: this.message,
                    name: this.name,
                    description: this.description,
                    number: this.number,
                    fileName: this.fileName,
                    lineNumber: this.lineNumber,
                    columnNumber: this.columnNumber,
                    stack: this.stack,
                    config: this.config,
                    code: this.code
                }
            },
            r
    }
    var U, F, V, z = M,
        J = ["age", "authorization", "content-length", "content-type", "etag", "expires", "from", "host", "if-modified-since", "if-unmodified-since", "last-modified", "location", "max-forwards", "proxy-authorization", "referer", "retry-after", "user-agent"],
        X = O.isStandardBrowserEnv() ? (F = /(msie|trident)/i.test(navigator.userAgent), V = document.createElement("a"), U = Y(window.location.href),
            function(t) {
                var e = O.isString(t) ? Y(t) : t;
                return e.protocol === U.protocol && e.host === U.host
            }) : function() {
            return ! 0
        };
    function Y(t) {
        var e = t;
        return F && (V.setAttribute("href", e), e = V.href),
            V.setAttribute("href", e),
            {
                href: V.href,
                protocol: V.protocol ? V.protocol.replace(/:$/, "") : "",
                host: V.host,
                search: V.search ? V.search.replace(/^\?/, "") : "",
                hash: V.hash ? V.hash.replace(/^#/, "") : "",
                hostname: V.hostname,
                port: V.port,
                pathname: "/" === V.pathname.charAt(0) ? V.pathname: "/" + V.pathname
            }
    }
    function $(p) {
        return new Promise(function(d, l) {
            var a = p.data,
                i = p.headers;
            O.isFormData(a) && delete i["Content-Type"];
            var f = new XMLHttpRequest;
            if (p.auth) {
                var t = p.auth.username || "",
                    e = p.auth.password || "";
                i.Authorization = "Basic " + btoa(t + ":" + e)
            }
            var n, r, s, o, c = (n = p.baseURL, r = p.url, n && !/^([a-z][a-z\d\+\-\.]*:)?\/\//i.test(r) ? (s = n, (o = r) ? s.replace(/\/+$/, "") + "/" + o.replace(/^\/+/, "") : s) : r);
            if (f.open(p.method.toUpperCase(), N(c, p.params, p.paramsSerializer), !0), f.timeout = p.timeout, f.onreadystatechange = function() {
                if (f && 4 === f.readyState && (0 !== f.status || f.responseURL && 0 === f.responseURL.indexOf("file:"))) {
                    var t, e, a, i, n, r, s, o, c, u = "getAllResponseHeaders" in f ? (t = f.getAllResponseHeaders(), n = {},
                        t && O.forEach(t.split("\n"),
                            function(t) {
                                if (i = t.indexOf(":"), e = O.trim(t.substr(0, i)).toLowerCase(), a = O.trim(t.substr(i + 1)), e) {
                                    if (n[e] && 0 <= J.indexOf(e)) return;
                                    n[e] = "set-cookie" === e ? (n[e] ? n[e] : []).concat([a]) : n[e] ? n[e] + ", " + a: a
                                }
                            }), n) : null,
                        h = {
                            data: p.responseType && "text" !== p.responseType ? f.response: f.responseText,
                            status: f.status,
                            statusText: f.statusText,
                            headers: u,
                            config: p,
                            request: f
                        };
                    r = d,
                        s = l,
                        !(c = (o = h).config.validateStatus) || c(o.status) ? r(o) : s(_("Request failed with status code " + o.status, o.config, null, o.request, o)),
                        f = null
                }
            },
                f.onabort = function() {
                    f && (l(_("Request aborted", p, "ECONNABORTED", f)), f = null)
                },
                f.onerror = function() {
                    l(_("Network Error", p, null, f)),
                        f = null
                },
                f.ontimeout = function() {
                    var t = "timeout of " + p.timeout + "ms exceeded";
                    p.timeoutErrorMessage && (t = p.timeoutErrorMessage),
                        l(_(t, p, "ECONNABORTED", f)),
                        f = null
                },
                O.isStandardBrowserEnv()) {
                var u = G,
                    h = (p.withCredentials || X(c)) && p.xsrfCookieName ? u.read(p.xsrfCookieName) : void 0;
                h && (i[p.xsrfHeaderName] = h)
            }
            if ("setRequestHeader" in f && O.forEach(i,
                function(t, e) {
                    void 0 === a && "content-type" === e.toLowerCase() ? delete i[e] : f.setRequestHeader(e, t)
                }), O.isUndefined(p.withCredentials) || (f.withCredentials = !!p.withCredentials), p.responseType) try {
                f.responseType = p.responseType
            } catch(t) {
                if ("json" !== p.responseType) throw t
            }
            "function" == typeof p.onDownloadProgress && f.addEventListener("progress", p.onDownloadProgress),
            "function" == typeof p.onUploadProgress && f.upload && f.upload.addEventListener("progress", p.onUploadProgress),
            p.cancelToken && p.cancelToken.promise.then(function(t) {
                f && (f.abort(), l(t), f = null)
            }),
            void 0 === a && (a = null),
                f.send(a)
        })
    }
    var G = O.isStandardBrowserEnv() ? {
            write: function(t, e, a, i, n, r) {
                var s = [];
                s.push(t + "=" + encodeURIComponent(e)),
                O.isNumber(a) && s.push("expires=" + new Date(a).toGMTString()),
                O.isString(i) && s.push("path=" + i),
                O.isString(n) && s.push("domain=" + n),
                !0 === r && s.push("secure"),
                    document.cookie = s.join("; ")
            },
            read: function(t) {
                var e = document.cookie.match(new RegExp("(^|;\\s*)(" + t + ")=([^;]*)"));
                return e ? decodeURIComponent(e[3]) : null
            },
            remove: function(t) {
                this.write(t, "", Date.now() - 864e5)
            }
        }: {
            write: function() {},
            read: function() {
                return null
            },
            remove: function() {}
        },
        K = {
            "Content-Type": "application/x-www-form-urlencoded"
        };
    function W(t, e) { ! O.isUndefined(t) && O.isUndefined(t["Content-Type"]) && (t["Content-Type"] = e)
    }
    var Z, Q = {
        adapter: (("undefined" != typeof XMLHttpRequest || "undefined" != typeof process && "[object process]" === Object.prototype.toString.call(process)) && (Z = $), Z),
        transformRequest: [function(t, e) {
            return I(e, "Accept"),
                I(e, "Content-Type"),
                O.isFormData(t) || O.isArrayBuffer(t) || O.isBuffer(t) || O.isStream(t) || O.isFile(t) || O.isBlob(t) ? t: O.isArrayBufferView(t) ? t.buffer: O.isURLSearchParams(t) ? (W(e, "application/x-www-form-urlencoded;charset=utf-8"), t.toString()) : O.isObject(t) ? (W(e, "application/json;charset=utf-8"), JSON.stringify(t)) : t
        }],
        transformResponse: [function(t) {
            if ("string" == typeof t) try {
                t = JSON.parse(t)
            } catch(t) {}
            return t
        }],
        timeout: 0,
        xsrfCookieName: "XSRF-TOKEN",
        xsrfHeaderName: "X-XSRF-TOKEN",
        maxContentLength: -1,
        validateStatus: function(t) {
            return 200 <= t && t < 300
        }
    };
    Q.headers = {
        common: {
            Accept: "application/json, text/plain, */*"
        }
    },
        O.forEach(["delete", "get", "head"],
            function(t) {
                Q.headers[t] = {}
            }),
        O.forEach(["post", "put", "patch"],
            function(t) {
                Q.headers[t] = O.merge(K)
            });
    var tt = Q;
    function et(t) {
        t.cancelToken && t.cancelToken.throwIfRequested()
    }
    function at(e) {
        return et(e),
            e.headers = e.headers || {},
            e.data = q(e.data, e.headers, e.transformRequest),
            e.headers = O.merge(e.headers.common || {},
                e.headers[e.method] || {},
                e.headers),
            O.forEach(["delete", "get", "head", "post", "put", "patch", "common"],
                function(t) {
                    delete e.headers[t]
                }),
            (e.adapter || tt.adapter)(e).then(function(t) {
                    return et(e),
                        t.data = q(t.data, t.headers, e.transformResponse),
                        t
                },
                function(t) {
                    return H(t) || (et(e), t && t.response && (t.response.data = q(t.response.data, t.response.headers, e.transformResponse))),
                        Promise.reject(t)
                })
    }
    function it(e, a) {
        a = a || {};
        var i = {},
            t = ["url", "method", "params", "data"],
            n = ["headers", "auth", "proxy"],
            r = ["baseURL", "url", "transformRequest", "transformResponse", "paramsSerializer", "timeout", "withCredentials", "adapter", "responseType", "xsrfCookieName", "xsrfHeaderName", "onUploadProgress", "onDownloadProgress", "maxContentLength", "validateStatus", "maxRedirects", "httpAgent", "httpsAgent", "cancelToken", "socketPath"];
        O.forEach(t,
            function(t) {
                void 0 !== a[t] && (i[t] = a[t])
            }),
            O.forEach(n,
                function(t) {
                    O.isObject(a[t]) ? i[t] = O.deepMerge(e[t], a[t]) : void 0 !== a[t] ? i[t] = a[t] : O.isObject(e[t]) ? i[t] = O.deepMerge(e[t]) : void 0 !== e[t] && (i[t] = e[t])
                }),
            O.forEach(r,
                function(t) {
                    void 0 !== a[t] ? i[t] = a[t] : void 0 !== e[t] && (i[t] = e[t])
                });
        var s = t.concat(n).concat(r),
            o = Object.keys(a).filter(function(t) {
                return - 1 === s.indexOf(t)
            });
        return O.forEach(o,
            function(t) {
                void 0 !== a[t] ? i[t] = a[t] : void 0 !== e[t] && (i[t] = e[t])
            }),
            i
    }
    function nt(t) {
        this.defaults = t,
            this.interceptors = {
                request: new z,
                response: new z
            }
    }
    nt.prototype.request = function(t, e) {
        "string" == typeof t ? (t = e || {}).url = arguments[0] : t = t || {},
            (t = it(this.defaults, t)).method ? t.method = t.method.toLowerCase() : this.defaults.method ? t.method = this.defaults.method.toLowerCase() : t.method = "get";
        var a = [at, void 0],
            i = Promise.resolve(t);
        for (this.interceptors.request.forEach(function(t) {
            a.unshift(t.fulfilled, t.rejected)
        }), this.interceptors.response.forEach(function(t) {
            a.push(t.fulfilled, t.rejected)
        }); a.length;) i = i.then(a.shift(), a.shift());
        return i
    },
        nt.prototype.getUri = function(t) {
            return t = it(this.defaults, t),
                N(t.url, t.params, t.paramsSerializer).replace(/^\?/, "")
        },
        O.forEach(["delete", "get", "head", "options"],
            function(a) {
                nt.prototype[a] = function(t, e) {
                    return this.request(O.merge(e || {},
                        {
                            method: a,
                            url: t
                        }))
                }
            }),
        O.forEach(["post", "put", "patch"],
            function(i) {
                nt.prototype[i] = function(t, e, a) {
                    return this.request(O.merge(a || {},
                        {
                            method: i,
                            url: t,
                            data: e
                        }))
                }
            });
    var rt = nt;
    function st(t) {
        this.message = t
    }
    st.prototype.toString = function() {
        return "Cancel" + (this.message ? ": " + this.message: "")
    },
        st.prototype.__CANCEL__ = !0;
    var ot = st;
    function ct(t) {
        if ("function" != typeof t) throw new TypeError("executor must be a function.");
        var e;
        this.promise = new Promise(function(t) {
            e = t
        });
        var a = this;
        t(function(t) {
            a.reason || (a.reason = new ot(t), e(a.reason))
        })
    }
    ct.prototype.throwIfRequested = function() {
        if (this.reason) throw this.reason
    },
        ct.source = function() {
            var e;
            return {
                token: new ct(function(t) {
                    e = t
                }),
                cancel: e
            }
        };
    var ut = ct;
    function dt(t) {
        var e = new rt(t),
            a = L(rt.prototype.request, e);
        return O.extend(a, rt.prototype, e),
            O.extend(a, e),
            a
    }
    var lt = dt(tt);
    lt.Axios = rt,
        lt.create = function(t) {
            return dt(it(lt.defaults, t))
        },
        lt.Cancel = ot,
        lt.CancelToken = ut,
        lt.isCancel = H,
        lt.all = function(t) {
            return Promise.all(t)
        },
        lt.spread = function(e) {
            return function(t) {
                return e.apply(null, t)
            }
        };
    var ft = lt,
        pt = lt;
    ft.
        default = pt;
    var vt = ft.create({
        baseURL: "http://localhost:8899/",
        timeout: 2e4,
        maxContentLength: 2e3,
        headers: {}
    });
    vt.interceptors.response.use(function(t) {
        var e = void 0;
        return 200 === t.status && (e = t.data),
            e
    });
    function mt(t) {
        return yt.has(t) || yt.set(t, {}),
            yt.get(t)
    }
    var yt = new WeakMap,
        gt = "http://127.0.0.1:8080";
    gt = "https:" == window.location.protocol ? "https://27.223.29.134:7026": "http://27.223.29.134:7025";
    var bt = (e(Dt, [{
        key: "requestAllData",
        value: function() {
            return vt({
                method: "get",
                url: mt(this).areaCount
            })
        }
    },
        {
            key: "requestLocalData",
            value: function(e) {
                ht.Default.xhrLoad(mt(this).localData,
                    function(t) {
                        e(t)
                    })
            }
        },
        {
            key: "requestHistoryData",
            value: function(t) {
                return vt({
                    method: "get",
                    url: mt(this).areaCount + "?date=" + t
                })
            }
        }]), Dt);
    function Dt() {
        a(this, Dt),
            mt(this).localData = "assets/dataSource2.json",
            mt(this).dayCount = gt + "/dayInfo",
            mt(this).areaCount = gt + "/areaInfo"
    }
    var wt, Tt, St, kt, xt = (n(Lt, k), e(Lt, [{
        key: "onPostDeserialize",
        value: (kt = t(regeneratorRuntime.mark(function t() {
            return regeneratorRuntime.wrap(function(t) {
                    for (;;) switch (t.prev = t.next) {
                        case 0:
                            return this.handleSwitch = function() {},
                                this.dm.each(function(t) {
                                    t.s("cache", !0)
                                }),
                                this.nodeSelector(),
                                t.next = 5,
                                this.request();
                        case 5:
                            u.fire("show3d", {
                                data: this.areaDatas,
                                obj: this.detailTable
                            });
                        case 6:
                        case "end":
                            return t.stop()
                    }
                },
                t, this)
        })),
            function() {
                return kt.apply(this, arguments)
            })
    },
        {
            key: "nodeSelector",
            value: function() {
                var t = this.dm;
                this.detailTable = t.getDataByTag("table1"),
                    this.newsTable = t.getDataByTag("table2"),
                    this.chartTitle1 = t.getDataByTag("title1"),
                    this.chartTitle2 = t.getDataByTag("title2"),
                    this.chartTitle3 = t.getDataByTag("title3"),
                    this.pieTitle = t.getDataByTag("pieTitle"),
                    this.chart1 = t.getDataByTag("right1"),
                    this.chart2 = t.getDataByTag("right2"),
                    this.chart3 = t.getDataByTag("right3"),
                    this.chart4 = t.getDataByTag("pie"),
                    this.bottom = t.getDataByTag("bottom"),
                    this.dateSlider = t.getDataByTag("slider"),
                    this.dateSlider1 = t.getDataByTag("sliderTime"),
                    this.playMenu = t.getDataByTag("play"),
                    this.display = t.getDataByTag("display"),
                    this.display2 = t.getDataByTag("display2"),
                    this.display3 = t.getDataByTag("display3")
            }
        },
        {
            key: "request",
            value: (St = t(regeneratorRuntime.mark(function t() {
                var a = this;
                return regeneratorRuntime.wrap(function(t) {
                        for (;;) switch (t.prev = t.next) {
                            case 0:
                                if ("api" === this.requestMode) return t.next = 3,
                                    this.service.requestAllData();
                                t.next = 8;
                                break;
                            case 3:
                                this.response = t.sent,
                                    this.response = JSON.parse(this.response.data),
                                    this.afterRequest(),
                                    t.next = 9;
                                break;
                            case 8:
                                this.service.requestLocalData(function(t) {
                                    var e = JSON.parse(t);
                                    a.response = JSON.parse(e.data),
                                        a.afterRequest()
                                });
                            case 9:
                            case "end":
                                return t.stop()
                        }
                    },
                    t, this)
            })),
                function() {
                    return St.apply(this, arguments)
                })
        },
        {
            key: "afterRequest",
            value: function() {
                this.initData(this.response),
                    this.initEvent(),
                    this.initCharts(),
                    this.initTables(),
                    this.initRest(),
                    this.playTable()
            }
        },
        {
            key: "initData",
            value: function(t) {
                var e = this.lastUpdateTime = t.lastUpdateTime,
                    a = t.chinaTotal,
                    i = t.chinaAdd,
                    n = e.slice(5, 10),
                    r = this.dateList = [],
                    s = this.confirmList = [],
                    o = this.suspectList = [],
                    c = this.confirmAddList = [],
                    u = this.suspectAddList = [],
                    h = this.deadList = [],
                    d = this.healList = [],
                    l = t.dailyHistory.slice( - 10),
                    f = t.dailyNewAddHistory.slice( - 10),
                    p = this.notHubeiDateList = [],
                    v = this.notHubeiAddList = [],
                    m = this.notHubeiDeadRateList = [],
                    y = this.notHubeiHealRateList = [];
                l.forEach(function(t) {
                    p.push(t.date),
                        m.push(t.notHubei.deadRate),
                        y.push(t.notHubei.healRate)
                }),
                    f.forEach(function(t) {
                        v.push(t.notHubei)
                    });
                var g = this.confirmToday = a.confirm,
                    b = this.suspectToday = a.suspect,
                    D = this.deadToday = a.dead,
                    w = this.healToday = a.heal,
                    T = i.dead,
                    S = i.heal,
                    k = i.confirm,
                    x = i.suspect;
                this.rest = this.confirmToday - this.deadToday - this.healToday;
                var L = t.chinaDayList.slice( - 9),
                    E = t.chinaDayAddList.slice( - 9);
                L.forEach(function(t) {
                    r.push(t.date),
                        s.push(t.confirm),
                        o.push(t.suspect),
                        h.push(t.dead),
                        d.push(t.heal)
                }),
                    r.push(n),
                    s.push(g),
                    o.push(b),
                    h.push(D),
                    d.push(w),
                    E.forEach(function(t) {
                        c.push(Math.abs(t.confirm)),
                            u.push(Math.abs(t.suspect))
                    }),
                    c.push(Math.abs(k)),
                    u.push(Math.abs(x));
                var C = this.sliderDateList = [],
                    A = this.sliderDayList = [],
                    R = this.sliderDayAddList = [],
                    B = t.chinaDayList.slice( - 17),
                    P = t.chinaDayAddList.slice( - 17);
                B.forEach(function(t) {
                    C.push(t.date),
                        A.push({
                            confirm: t.confirm,
                            suspect: t.suspect,
                            heal: t.heal,
                            dead: t.dead
                        })
                }),
                    C.push(n),
                    A.push({
                        confirm: g,
                        suspect: b,
                        heal: w,
                        dead: D
                    }),
                    P.forEach(function(t) {
                        R.push({
                            confirm: t.confirm,
                            suspect: t.suspect,
                            heal: t.heal,
                            dead: t.dead
                        })
                    }),
                    R.push({
                        confirm: k,
                        suspect: x,
                        heal: S,
                        dead: T
                    });
                var O = t.articleList,
                    j = this.articleDataSource = [];
                O.forEach(function(t) {
                    j.push({
                        date: t.publish_time,
                        content: t.title
                    })
                });
                var N = t.areaTree[0].children,
                    M = this.areaDatas = [];
                N.forEach(function(t) {
                    var e = t.name;
                    M.push({
                        area: e,
                        host: "中国",
                        expand: !1,
                        areaType: "province",
                        confirm: t.total.confirm,
                        cured: t.total.heal,
                        death: t.total.dead
                    }),
                        t.children.forEach(function(t) {
                            M.push({
                                area: t.name,
                                host: e,
                                areaType: "city",
                                expand: !1,
                                confirm: t.total.confirm,
                                cured: t.total.heal,
                                death: t.total.dead
                            })
                        })
                }),
                    this.provinceDatas = {}
            }
        },
        {
            key: "initEvent",
            value: function() {
                var e = this;
                u.add("renderMap",
                    function(t) {
                        t.dm && (e.dm3d = t.dm),
                            e.renderProvinceColor(t.index)
                    }),
                    u.add("resetMap",
                        function(t) {
                            e.areaDatas.forEach(function(t) {
                                t.expand = !1
                            }),
                                t.forEach(function(t) {
                                    t.s("shape3d.opacity", 1)
                                }),
                                e.detailTable.a("ht.dataSource", e.areaDatas)
                        }),
                    u.add("clickProvince",
                        function(t) {
                            e.clickProvince(t.data, t.node)
                        })
            }
        },
        {
            key: "initCharts",
            value: function() {
                this.chart1.a({
                    // axisData: this.dateList,
                    // seriesData1: this.suspectList,
                    // seriesData2: this.confirmList
                }),
                    this.chart1.s("state", "确诊疑似"),
                    this.chartTitle1.s("text", "全国-累计确诊/疑似"),
                    this.chart2.a({
                        axisData: this.dateList,
                        seriesData1: this.confirmAddList,
                        seriesData2: this.suspectAddList
                    }),
                    this.chart2.s("state", "新增疑似"),
                    this.chartTitle2.s("text", "全国-新增疑似/确诊"),
                    this.chart3.a({
                        axisData: this.dateList,
                        seriesData1: this.healList,
                        seriesData2: this.deadList
                    }),
                    this.chart3.s("state", "治愈死亡"),
                    this.chartTitle3.s("text", "全国-累计治愈/死亡"),
                    this.pieTitle.s("2d.visible", !0),
                    this.chart4.s("2d.visible", !0),
                    this.chart4.a({
                        "ht.dataSource": [{
                            name: "治愈",
                            value: this.healToday
                        },
                            {
                                name: "死亡",
                                value: this.deadToday
                            },
                            {
                                name: "观察中",
                                value: this.rest
                            }],
                        confirm: this.confirmToday,
                        cured: this.healToday,
                        death: this.deadToday,
                        rest: this.rest
                    })
            }
        },
        {
            key: "initChartsExceptHubei",
            value: function() {
                this.chart1.a({
                    axisData: this.notHubeiDateList,
                    seriesData1: this.notHubeiHealRateList,
                    seriesData2: ""
                }),
                    this.chart1.s("state", "鄂外治愈"),
                    this.chartTitle1.s("text", "鄂外治愈率"),
                    this.chart2.a({
                        axisData: this.notHubeiDateList,
                        seriesData1: this.notHubeiAddList,
                        seriesData2: ""
                    }),
                    this.chart2.s("state", "鄂外新增确诊"),
                    this.chartTitle2.s("text", "鄂外新增确诊"),
                    this.chart3.a({
                        axisData: this.notHubeiDateList,
                        seriesData1: "",
                        seriesData2: this.notHubeiDeadRateList
                    }),
                    this.chart3.s("state", "鄂外死亡"),
                    this.chartTitle3.s("text", "鄂外死亡率"),
                    this.pieTitle.s("2d.visible", !1),
                    this.chart4.s("2d.visible", !1)
            }
        },
        {
            key: "initTables",
            value: function() {
                this.newsTable.a("ht.dataSource", this.articleDataSource),
                    this.detailTable.a("ht.dataSource", this.areaDatas)
            }
        },
        {
            key: "initRest",
            value: function() {
                var a = this,
                    t = this.dm.getDataByTag("updateTime"),
                    i = this.dateSlider,
                    e = this.dateSlider1;
                t.s("text", this.lastUpdateTime),
                    this.initBottom(17),
                    i.a({
                        text: this.sliderDateList[17],
                        dateArea: [385, 18, 61, 15]
                    }),
                    e.a("ht.onChange",
                        function(t, e) {
                            i.a({
                                text: a.sliderDateList[e],
                                dateArea: [e * y + 15, 18, 61, 15]
                            }),
                                a.initBottom(e),
                                a.updateChart(e),
                                u.fire("renderMap", {
                                    index: S(e - 17)
                                })
                        }),
                    this.sliderDateList.forEach(function(t) {
                        a.dateSlider.a("date." + (a.sliderDateList.indexOf(t) + 1), t)
                    })
            }
        },
        {
            key: "initBottom",
            value: function(t) {
                var e = this.sliderDayList[t],
                    a = this.sliderDayAddList[t];
                this.bottom.a({
                    confirm: e.confirm,
                    suspect: e.suspect,
                    heal: e.heal,
                    dead: e.dead,
                    confirmAdd: a.confirm,
                    suspectAdd: a.suspect,
                    healAdd: a.heal,
                    deadAdd: a.dead
                }),
                "country" === this.chartState && this.pieSetter()
            }
        },
        {
            key: "requestProvinceHistory",
            value: (Tt = t(regeneratorRuntime.mark(function t(e) {
                var a;
                return regeneratorRuntime.wrap(function(t) {
                        for (;;) switch (t.prev = t.next) {
                            case 0:
                                return t.next = 2,
                                    this.service.requestHistoryData(e.full);
                            case 2:
                                a = t.sent,
                                    a = JSON.parse(a.data),
                                    this.historyDatas = a.areaTree[0].children;
                            case 5:
                            case "end":
                                return t.stop()
                        }
                    },
                    t, this)
            })),
                function(t) {
                    return Tt.apply(this, arguments)
                })
        },
        {
            key: "renderProvinceColor",
            value: (wt = t(regeneratorRuntime.mark(function t(e) {
                var i = this;
                return regeneratorRuntime.wrap(function(t) {
                        for (;;) switch (t.prev = t.next) {
                            case 0:
                                return t.next = 2,
                                    this.requestProvinceHistory(e);
                            case 2:
                                this.historyDatas.forEach(function(t) {
                                    var e = t.total.confirm,
                                        a = i.dm3d.getDataByTag(t.name);
                                    1e4 < e ? a.s("shape3d.blend", "rgb(60,98,250)") : 1e3 < e ? a.s("shape3d.blend", "rgb(95,123,222)") : 500 < e ? a.s("shape3d.blend", "rgb(153,168,222)") : 100 < e ? a.s("shape3d.blend", "rgb(206,214,245)") : a.s("shape3d.blend", m)
                                });
                            case 3:
                            case "end":
                                return t.stop()
                        }
                    },
                    t, this)
            })),
                function(t) {
                    return wt.apply(this, arguments)
                })
        },
        {
            key: "playTable",
            value: function() {
                var a = this,
                    i = this.newsTable;
                ht.Default.startAnim({
                    duration: 1e3,
                    easing: function(t) {
                        return t
                    },
                    finishFunc: function() {
                        var t = i.a("ht.dataSource"),
                            e = t.shift();
                        t.push(e),
                            i.a("ht.dataSource", t),
                            i.a("ht.translateY", 0),
                            setTimeout(function() {
                                    a.playTable()
                                },
                                1e3)
                    },
                    action: function(t, e) {
                        i.a("ht.translateY", -70 * e)
                    }
                })
            }
        },
        {
            key: "stopDateSlider",
            value: function() {
                this.playerInterval && (clearInterval(this.playerInterval), this.playerInterval = void 0),
                    this.playMenu.s("state", "播放"),
                    this.onPlay = !1
            }
        },
        {
            key: "playDateSlider",
            value: function() {
                var a = this;
                this.playMenu.s("state", "暂停"),
                    this.onPlay = !0;
                var i = this.dateSlider,
                    n = this.dateSlider1;
                this.playerInterval = setInterval(function() {
                        var t = n.a("ht.value"),
                            e = 17 === t ? 0 : t + 1;
                        i.a({
                            text: a.sliderDateList[e],
                            dateArea: [e * y + 15, 18, 61, 15]
                        }),
                            n.a({
                                "ht.value": e
                            }),
                            a.initBottom(e),
                            a.updateChart(e),
                            u.fire("renderMap", {
                                index: S(e - 17)
                            })
                    },
                    1500)
            }
        },
        {
            key: "pieSetter",
            value: function() {
                var t = this.bottom.a("confirm") - this.bottom.a("heal") - this.bottom.a("dead");
                this.chart4.a({
                    "ht.dataSource": [{
                        name: "治愈",
                        value: this.bottom.a("heal")
                    },
                        {
                            name: "死亡",
                            value: this.bottom.a("dead")
                        },
                        {
                            name: "观察中",
                            value: t
                        }],
                    confirm: this.bottom.a("confirm"),
                    cured: this.bottom.a("heal"),
                    death: this.bottom.a("dead"),
                    rest: t
                })
            }
        },
        {
            key: "chartSetter",
            value: function() {}
        },
        {
            key: "updateChart",
            value: function(t) {
                "country" === this.chartState ? (this.currentDay = this.sliderDateList[t], this.chartSetter(this.chart1, t, this.suspectList, this.confirmList), this.chartSetter(this.chart2, t, this.confirmAddList, this.suspectAddList), this.chartSetter(this.chart3, t, this.healList, this.deadList)) : "exceptHubei" === this.chartState && (this.currentDay = this.sliderDateList[t], this.chartSetter(this.chart1, t), this.chartSetter(this.chart2, t), this.chartSetter(this.chart3, t))
            }
        },
        {
            key: "clickProvince",
            value: function(t, e) {
                var a = e.getDisplayName();
                if (t.forEach(function(t) {
                    t.s("shape3d.opacity", t === e ? 1 : .4)
                }), this.areaDatas && 0 < this.areaDatas.length) {
                    var i = [];
                    this.areaDatas.forEach(function(t) {
                        t.area !== a && t.host !== a || (t.expand = !0, i.push(t))
                    }),
                        this.detailTable.a("ht.dataSource", i)
                }
            }
        },
        {
            key: "handleInteractive",
            value: function(t) {
                var e = t.data,
                    a = t.kind,
                    i = this.dm;
                if (e) {
                    var n = e.getTag();
                    n && "clickData" === a && ("btn1" === n || "btn2" === n || "btn3" === n ? (i.getDataByTag("data_switch_button").eachChild(function(t) {
                        t.s("state", e === t ? "点击": "未点击")
                    }), "btn1" === n ? (this.initCharts(), this.chartState = "country") : "btn2" === n && (this.initChartsExceptHubei(), this.chartState = "exceptHubei")) : "play" === n && (this.onPlay ? this.stopDateSlider() : this.playDateSlider()))
                }
            }
        }]), Lt);
    function Lt(t) {
        a(this, Lt);
        var e = s(this, (Lt.__proto__ || Object.getPrototypeOf(Lt)).call(this, t));
        return e.requestMode = "api",
            e.service = new bt,
            e.key = "2D",
            e.url = "displays/htdesign/2020/新冠病毒/PC版白.json",
            e.active = !1,
            e.view = t,
            e.dm = t.dm(),
            e.chartState = "country",
            e.sliderValue = 100,
            e.onPlay = !1,
            e.currentDay = "02-22",
            e
    }
    var Et = (n(Ct, k), e(Ct, [{
        key: "initEvent",
        value: function() {
            var e = this;
            u.add("show3d",
                function(t) {
                    e.areaDatas = t.data,
                        e.detailTable = t.obj,
                        u.fire(h, "3D")
                }),
                u.add("resetMapColor",
                    function() {
                        e.g3dProvince.forEach(function(t) {
                            t.s("shape3d.blend", m)
                        })
                    })
        }
    },
        {
            key: "start3dAnim",
            value: function() {
                var t = this.dm,
                    i = 0,
                    n = 0,
                    r = 2,
                    s = 2,
                    e = {
                        interval: 30,
                        action: function(t) {
                            var e = t.getDisplayName(),
                                a = t.getTag();
                            e && ("城市点" === e ? t.setRotationY(n) : "movingLine" === e ? t.s("shape3d.uv.offset", [ - i, 0]) : "起点" === e && t.setTall(r)),
                            a && "wuhanCylinder" === a && t.setTall(s)
                        },
                        afterAction: function() {
                            n += .01,
                                i += .01,
                            20 < (r += .2) && (r = 2),
                            60 < (s += .6) && (s = 2)
                        }
                    };
                t.addScheduleTask(e)
            }
        },
        {
            key: "init3dNodes",
            value: function() {
                var a = this,
                    i = this.g3dProvince = [];
                d.forEach(function(t) {
                    var e = a.dm.getDataByTag(t);
                    e.s("select.brightness", 1),
                        i.push(e)
                })
            }
        },
        {
            key: "onPostDeserialize",
            value: function() {
                var t = this,
                    e = this.view,
                    a = this.dm;
                this.handleSwitch = function() {},
                    a.each(function(t) {
                        t.s({
                            "geometry.cache": !0,
                            "shape3d.image.cache": !0
                        })
                    }),
                    this.init3dNodes(),
                    setTimeout(function() {
                            e.moveCamera(p, v, {
                                duration: 3e3,
                                easing: w.swing,
                                finishFunc: function() {
                                    t.start3dAnim()
                                }
                            })
                        },
                        1500),
                    u.fire("renderMap", {
                        dm: a,
                        index: S( - 1)
                    });
                var i = new ht.graph3d.MapInteractor(e);
                e.setInteractors([i]),
                    i.maxPhi = 2 * Math.PI / 5
            }
        },
        {
            key: "handleInteractive",
            value: function(t) {
                var e = t.data,
                    a = t.kind;
                if ("clickBackground" === a && u.fire("resetMap", this.g3dProvince), e) {
                    var i = e.getDisplayName(); - 1 < d.indexOf(i) && ("onEnter" === a ? e.s("wf.color", "rgb(250,250,87)") : "onLeave" === a ? e.s("wf.color", "rgb(61,61,61)") : "clickData" === a && u.fire("clickProvince", {
                        data: this.g3dProvince,
                        node: e
                    }))
                }
            }
        }]), Ct);
    function Ct(t) {
        a(this, Ct);
        var e = s(this, (Ct.__proto__ || Object.getPrototypeOf(Ct)).call(this, t));
        return e.key = "3D",
            e.url = "scenes/htdesign/地图/3d中国.json",
            e.active = !1,
            e.view = t,
            e.dm = t.dm(),
            e.cityPoints = [],
            e.cylinders = [],
            e.movingLines = [],
            e.initEvent(),
            e
    }
    function At() {
        a(this, At);
        this.g2d = new ht.graph.GraphView,
            this.g3d = new ht.graph3d.Graph3dView;
        this.event = u,
            this.initView()
    }
    return new(e(At, [{
        key: "initView",
        value: function() {
            var e = this.g2d,
                t = this.g3d;
            function a(t) {
                e.getDataAt(t) && t.stopPropagation()
            }
            e.getSelectWidth = function() {
                return 0
            },
                e.setZoomable(!1),
                e.setPannable(!1),
                e.setRectSelectable(!1),
                e.setScrollBarVisible(!1),
                e.setMovableFunc(function() {
                    return ! 1
                }),
                e.getView().addEventListener("mousedown", a),
                e.getView().addEventListener("touchstart", a),
                e.getView().addEventListener("wheel", a),
                e.addToDOM(t.getView()),
                t.addToDOM(),
                t.setEye(l),
                t.setCenter(f),
                new xt(e),
                new Et(t),
                this.
                switch ()
        }
    },
        {
            key: "switch",
            value: function(t) {
                var e = 0 < arguments.length && void 0 !== t ? t: "2D";
                u.fire(h, e)
            }
        }]), At)
} ();
