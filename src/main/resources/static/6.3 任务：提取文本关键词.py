# 代码6–1
import jieba
import jieba.posseg
import numpy as np
import pandas as pd
import math
import operator
'''
提供Python内置的部分操作符函数，这里主要应用于序列操作
用于对大型语料库进行主题建模，支持TF-IDF、LSA和LDA等多种主题模型算法，提供了
诸如相似度计算、信息检索等一些常用任务的API接口
'''
from gensim import corpora, models
text = '广州地铁集团工会主席钟学军在开幕式上表示，在交通强国战略的指引下，我国城市轨道' \
       '交通事业蓬勃发展，城轨线路运营里程不断增长，目前，全国城市轨道交通线网总里程' \
       '接近5000公里，每天客运量超过5000万人次。城市轨道交通是高新技术密集型行业，' \
       '几十个专业纷繁复杂，几十万台（套）设备必须安全可靠，线网调度必须联动周密，' \
       '列车运行必须精准分秒不差。城市轨道交通又是人员密集型行业，产业工人素质的好坏、' \
       '高低，直接与人民生命安全息息相关。本届“国赛”选取的列车司机和行车值班员，' \
       '正是行业安全运营的核心、关键工种。开展职业技能大赛的目的，就是要弘扬' \
       '“工匠精神”，在行业内形成“比、学、赶、帮、超”的良好氛围，在校园里掀起' \
       '“学本领、争上游”的学习热潮，共同为我国城市轨道交通的高质量发展和交通强国' \
       '建设目标的全面实现做出应有的贡献。'



# 代码6–2
# 获取停用词
def Stop_words():
    stopword = []
    data = []
    f = open('../data/stopword.txt', encoding='utf8')
    for line in f.readlines():
        data.append(line)
    for i in data:
        output = str(i).replace('\n', '')
        stopword.append(output)
    return stopword

# 采用jieba进行词性标注，对当前文档过滤词性和停用词
def Filter_word(text):
    filter_word = []
    stopword = Stop_words()
    text = jieba.posseg.cut(text)
    for word, flag in text:
        if flag.startswith('n') is False:
            continue
        if not word in stopword and len(word) > 1:
            filter_word.append(word)
    return filter_word
# 加载文档集，对文档集过滤词性和停用词
def Filter_words(data_path = '../data/corpus.txt'):
    document = []
    for line in open(data_path, 'r', encoding='utf8'):
        segment = jieba.posseg.cut(line.strip())
        filter_words = []
        stopword = Stop_words()
        for word, flag in segment:
            if flag.startswith('n') is False:
                continue
            if not word in stopword and len(word) > 1:
                filter_words.append(word)
        document.append(filter_words)
    return document



# 代码6–3
# TF-IDF 算法
def tf_idf():
    # 统计TF值
    tf_dict = {}
    filter_word = Filter_word(text)
    for word in filter_word:
        if word not in tf_dict:
            tf_dict[word] = 1
        else:
            tf_dict[word] += 1
    for word in tf_dict:
        tf_dict[word] = tf_dict[word] / len(text)
    # 统计IDF值
    idf_dict = {}
    document = Filter_words()
    doc_total = len(document)
    for doc in document:
        for word in set(doc):
            if word not in idf_dict:
                idf_dict[word] = 1
            else:
                idf_dict[word] += 1
    for word in idf_dict:
        idf_dict[word] = math.log(doc_total / (idf_dict[word] + 1))
    # 计算TF-IDF值
    tf_idf_dict = {}
    for word in filter_word:
        if word not in idf_dict:
            idf_dict[word] = 0
        tf_idf_dict[word] = tf_dict[word] * idf_dict[word]
    # 提取前10个关键词
    keyword = 10
    print('TF-IDF模型结果：')
    for key, value in sorted(tf_idf_dict.items(), key=operator.itemgetter(1),
                             reverse=True)[:keyword]:
        print(key + '/', end='')



# 代码6–4
def TextRank():
    window = 3
    win_dict = {}
    filter_word = Filter_word(text)
    length = len(filter_word)
    # 构建每个节点的窗口集合
    for word in filter_word:
        index = filter_word.index(word)
       # 设置窗口左、右边界，控制边界范围
        if word not in win_dict:
            left = index - window + 1
            right = index + window
            if left < 0:
                left = 0
            if right >= length:
                right = length
            words = set()
            for i in range(left, right):
                if i == index:
                    continue
                words.add(filter_word[i])
                win_dict[word] = words
    # 构建相连的边的关系矩阵
    word_dict = list(set(filter_word))
    lengths = len(set(filter_word))
    matrix = pd.DataFrame(np.zeros([lengths,lengths]))
    for word in win_dict:
        for value in win_dict[word]:
            index1 = word_dict.index(word)
            index2 = word_dict.index(value)
            matrix.iloc[index1, index2] = 1
            matrix.iloc[index2, index1] = 1
    summ = 0
    cols = matrix.shape[1]
    rows = matrix.shape[0]
    # 归一化矩阵
    for j in range(cols):
        for i in range(rows):
            summ += matrix.iloc[i, j]
        matrix[j] /= summ
    # 根据公式计算textrank值
    d = 0.85
    iter_num = 700
    word_textrank = {}
    textrank = np.ones([lengths, 1])
    for i in range(iter_num):
        textrank = (1 - d) + d * np.dot(matrix, textrank)
    # 将词语和textrank值一一对应
    for i in range(len(textrank)):
        word = word_dict[i]
        word_textrank[word] = textrank[i, 0]
    keyword = 10
    print('------------------------------')
    print('textrank模型结果：')
    for key, value in sorted(word_textrank.items(), key=operator.itemgetter(1),
                             reverse=True)[:keyword]:
        print(key + '/', end='')



# 代码6–5
def lsi():
    # 主题-词语
    document = Filter_words()
    dictionary = corpora.Dictionary(document)  # 生成基于文档集的语料
    corpus = [dictionary.doc2bow(doc) for doc in document]  # 文档向量化
    tf_idf_model = models.TfidfModel(corpus)  # 构建TF-IDF模型
    tf_idf_corpus = tf_idf_model[corpus]  # 生成文档向量
    lsi = models.LsiModel(tf_idf_corpus, id2word=dictionary, num_topics=4)
# 构建lsiLSI模型，函数包括3个参数：文档向量、文档集语料id2word和
# 主题数目num_topics，id2word可以将文档向量中的id转化为文字
    # 主题-词语
    words = []
    word_topic_dict = {}
    for doc in document:
        words.extend(doc)
        words = list(set(words))
    for word in words:
        word_corpus = tf_idf_model[dictionary.doc2bow([word])]
        word_topic= lsi[word_corpus]
        word_topic_dict[word] = word_topic
    # 文档-主题
    filter_word = Filter_word(text)
    corpus_word = dictionary.doc2bow(filter_word)
    text_corpus = tf_idf_model[corpus_word]
    text_topic = lsi[text_corpus]
    # 计算当前文档和每个词语的主题分布相似度
    sim_dic = {}
    for key, value in word_topic_dict.items():
        if key not in text:
            continue
        x = y = z = 0
        for tup1, tup2 in zip(value, text_topic):
            x += tup1[1] ** 2
            y += tup2[1] ** 2
            z += tup1[1] * tup2[1]
            if x == 0 or y == 0:
                sim_dic[key] = 0
            else:
                sim_dic[key] = z / (math.sqrt(x * y))
    keyword = 10
    print('------------------------------')
    print('LSI模型结果：')
    for key, value in sorted(sim_dic.items(), key=operator.itemgetter(1),
                            reverse=True)[: keyword]:
        print(key + '/' , end='')



# 代码6–6
tf_idf()
TextRank()
lsi()



