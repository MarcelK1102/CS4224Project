import pandas as pd
df = pd.read_csv("stock.csv")
df[['S_W_ID', 'S_I_ID', 'S_QUANTITY', 'S_YTD', 'S_ORDER_CNT', 'S_REMOTE_CNT']].to_csv("stock_cnts.csv")
df[['S_W_ID', 'S_I_ID', 'S_DIST_01', 'S_DIST_02', 'S_DIST_03', 'S_DIST_04', 'S_DIST_05', 'S_DIST_06', 'S_DIST_07', 'S_DIST_08', 'S_DIST_09', 'S_DIST_10', 'S_DATA']].to_csv("stock.csv")