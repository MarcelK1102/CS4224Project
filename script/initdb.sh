use warehouse
db.warehouse.createIndex({W_ID:1}, {unique:true})
db.district.createIndex({D_W_ID:1, D_ID:1}, {unique:true})
db.customer.createIndex({C_W_ID:1, C_D_ID:1, C_ID:1}, {unique:true})
db.order.createIndex({O_W_ID:1, O_D_ID:1, O_ID:1}, {unique:true})
db.item.createIndex({I_ID:1}, {unique:true})
db.order_line.createIndex({OL_W_ID:1, OL_D_ID:1, OL_O_ID:1, OL_NUMBER:1}, {unique:true})
db.stock.createIndex({S_W_ID:1, S_I_ID:1}, {unique:true})