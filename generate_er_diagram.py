from sqlalchemy import create_engine, MetaData
from sqlalchemy_schemadisplay import create_schema_graph

# 创建数据库连接
engine = create_engine('mysql+pymysql://username:password@localhost/dbname')

# 获取元数据
metadata = MetaData()
metadata.reflect(bind=engine)

# 生成ER图
graph = create_schema_graph(
    metadata=metadata,
    show_datatypes=False,  # 不显示数据类型
    show_indexes=False,    # 不显示索引
    rankdir='LR',          # 从左到右布局
    concentrate=False      # 不合并关系线
)

# 保存为图片
graph.write_png('er_diagram.png')