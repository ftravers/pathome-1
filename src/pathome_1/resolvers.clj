(ns pathome-1.resolvers
  (:require
   [com.wsscode.pathom3.connect.operation :as operation]
   [com.wsscode.pathom3.connect.indexes :as indexes]
   [com.wsscode.pathom3.interface.eql :as query]))

(def database
  {1 {::user-id 1
      ::name "joe"
      ::age 22
      ::cars [{::car-id 1 ::make "ford" ::model "mustang" ::year 1957}
              {::car-id 2 ::make "toyota" ::model "tacoma" ::year 2023}]}
   2 {::user-id 2
      ::name "bob"
      ::age 10
      ::cars [{::car-id 3 ::make "honda" ::model "civic" ::year 2018}]}})

(def users
  {1 {::user-id 1
      ::name "joe"
      ::age 22
      ::car-id [1 2]}
   2 {::user-id 2
      ::name "bob"
      ::age 10
      ::car-id [3]}})

(def cars
  {1 {::car-id 1 ::make "ford" ::model "mustang" ::year 1957}
   2 {::car-id 2 ::make "toyota" ::model "tacoma" ::year 2023}
   3 {::car-id 3 ::make "honda" ::model "civic" ::year 2018}})

(operation/defresolver id->user
  [{::keys [user-id]}]
  {::operation/output [::name
                       ::age
                       {::cars [::car-id
                                ::make
                                ::model
                                ::year]}]}
  (let [{car-ids ::car-id
         :as user} (get users user-id)]
    (assoc user ::cars (map #(get cars %) car-ids))))

(id->user {::user-id 1})
;; => #:pathome-1.resolvers{:user-id 1,
;;                          :name "joe",
;;                          :age 22,
;;                          :car-id [1 2],
;;                          :cars
;;                          (#:pathome-1.resolvers{:car-id 1,
;;                                                 :make "ford",
;;                                                 :model "mustang",
;;                                                 :year 1957}
;;                           #:pathome-1.resolvers{:car-id 2,
;;                                                 :make "toyota",
;;                                                 :model "tacoma",
;;                                                 :year 2023})}

(operation/defresolver id->car
  [{::keys [car-id]}]
  {::operation/output [::make ::model ::year]}
  (get cars car-id))

(def index (indexes/register [id->user id->car]))

(query/process index [{[::user-id 1] [::name {::cars [::make]}]}])
;; => {[:pathome-1.resolvers/user-id 1]
;;     #:pathome-1.resolvers{:name "joe",
;;                           :cars
;;                           (#:pathome-1.resolvers{:make "ford"}
;;                            #:pathome-1.resolvers{:make "toyota"})}}





