import {  BlankLayout,HeavyDutyLayout } from '@/components/layouts'
const router = {
  "TableRouter": {
    path: '/report',
    component: BlankLayout,
    children: [
      {
        path: "singleProjectTable",
        name: "singleProjectTable",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/SingleProjectTable')
      },
      {
        path: "singleProjectTableFixed",
        name: "singleProjectTableFixed",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/SingleProjectTableFixed')
      },
      {
        path: "otDeliverableList",
        name: "otDeliverableList",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/OtDeliverableList')
      },
      {
        path: "inRatingList",
        name: "inRatingList",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/InRatingList')
      },
      {
        path: "riskMeasureList",
        name: "riskMeasureList",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/RiskMeasureList')
      },
      {
        path: "riskTable",
        name: "riskTable",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/RiskTable')
      },
      {
        path: "sncsList",
        name: "sncsList",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/XncsList')
      },
      {
        path: "issueRiskChain",
        name: "issueRiskChain",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/IssueRiskChain')
      },
      {
        path: "dpmReport",
        name: "dpmReport",
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/DpmReport')
      }
    ]
  },
  "ReportRouter":  {
    path: '/report',
    name: 'report',
    component: HeavyDutyLayout,
    // component: BlankLayout,
    children: [
      {
        path: 'projectRisk',
        name: 'projectRisk',
        component: () => import(/* webpackChunkName: "report" */  '@/views/report/ProjectRisk')
      },
      {
        path: 'departmentRisk',
        name: 'departmentRisk',
        //  component: () => import(/* webpackChunkName: "report" */ '@/views/report/DepartmentRisk')
         component: () => import(/* webpackChunkName: "report" */ '@/views/report/DepartmentRiskScroll')
      },
      {
        path: 'pertReport',
        name: 'pertReport',
         component: () => import(/* webpackChunkName: "report" */  '@/views/report/PertReport')
      },
      {
        path: 'taskDelayReport',
        name: 'taskDelayReport',
         component: () => import(/* webpackChunkName: "report" */  '@/views/report/TaskDelayReport')
      },
      // {
      //   path: 'singleProjectTable',
      //   name: 'singleProjectTable',
      //    component: () => import(/* webpackChunkName: "report" */  '@/views/report/SingleProjectTable1')
      // },
    ],
    
  }

}

export default router