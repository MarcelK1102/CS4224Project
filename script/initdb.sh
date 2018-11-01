use warehouse
db.createCollection("warehouse", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: [ "W_ID", "W_NAME", "W_STREET_1", "W_STREET_2", "W_CITY", "W_STATE", "W_ZIP", "W_TAX", "W_YTD" ],
         properties: {
 			W_ID : {
				bsonType: "int"
			} ,
    		W_NAME : {
				bsonType: "string"
			} ,
    		W_STREET_1 : {
				bsonType: "string"
			} ,
    		W_STREET_2 : {
				bsonType: "string"
			} ,
    		W_CITY : {
				bsonType: "string"
			} ,
    		W_STATE : {
				bsonType: "string"
			} ,
    		W_ZIP : {
				bsonType: "string"
			},
    		W_TAX : {
				bsonType: "double"
			},
    		W_YTD : {
				bsonType: "double"
			}
         }
      }
   }
})

db.createCollection("district", {
   validator: {
      $jsonSchema: {
		bsonType: "object",
		required: [ "D_W_ID", "D_ID", "D_NAME", "D_STREET_1", "D_STREET_2", "D_CITY", "D_STATE", "D_ZIP", "D_TAX", "D_YTD", "D_NEXT_O_ID"],
		properties: {
			D_W_ID : {
				bsonType:"int"
			},
			D_ID : {
				bsonType:"int"
			},
			D_NAME : {
				bsonType:"string"
			},
			D_STREET_1 : {
				bsonType:"string"
			},
			D_STREET_2 : {
				bsonType:"string"
			},
			D_CITY : {
				bsonType:"string"
			},
			D_STATE : {
				bsonType:"string"
			},
			D_ZIP : {
				bsonType:"string"
			},
			D_TAX : {
				bsonType:"double"
			},
			D_YTD : {
				bsonType:"double"
			},
			D_NEXT_O_ID : {
				bsonType:"int"
			}
		}
      }
   	}
})


db.warehouse.createIndex({W_ID:1}, {unique:true})
db.district.createIndex({D_W_ID:1, D_ID:1}, {unique:true})
db.customer.createIndex({C_W_ID:1, C_D_ID:1, C_ID:1}, {unique:true})
db.order.createIndex({O_W_ID:1, O_D_ID:1, O_ID:1}, {unique:true})
db.item.createIndex({I_ID:1}, {unique:true})
db.order_line.createIndex({OL_W_ID:1, OL_D_ID:1, OL_O_ID:1, OL_NUMBER:1}, {unique:true})
db.stock.createIndex({S_W_ID:1, S_I_ID:1}, {unique:true})
db.order_line.createIndex({OL_QUANTITY:-1},{})
db.order.createIndex({O_ENTRY_D:-1},{})
