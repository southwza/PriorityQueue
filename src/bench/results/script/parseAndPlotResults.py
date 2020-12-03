import re
from collections import defaultdict
import plotly.graph_objects as go
import plotly.offline as offline

file = open("results/raw/Bench_results_macbook-pro-12core.txt", 'r')

def getFormattedTestResultList(testResults):
    formattedTestResultList = []
    for result in testResults:
        currentResult = result.split(delimiter)
        subResult = currentResult[0] + delimiter + currentResult[1] + delimiter + currentResult[4]
        formattedTestResultList.append(cleanUpTestResult(subResult))
    return formattedTestResultList

def cleanUpTestResult(result):
    testCase = result.split(delimiter)
    testCasesplitValues = testCase[0].split('_')
    testCaseAlgorithm = testCasesplitValues[0].split('.')[2].replace("bench", "")
    testNumberOfThreads = re.sub("[^0-9]", "", testCasesplitValues[1])
    testCaseOperation = testCasesplitValues[2]

    testCaseScenario = testCase[1].replace("src/bench/benchCases/", "").split('_')[0]
    testCaseDataSize = testCase[1].replace("src/bench/benchCases/", "").split('_')[1].replace(".txt", "")
    testDurationInUs = testCase[2].replace(',', '')

    return [testCaseAlgorithm + delimiter + testNumberOfThreads + delimiter + testCaseOperation + delimiter + testCaseScenario + delimiter + testCaseDataSize + delimiter + testDurationInUs]

def savePlotGroupedResults(results, dataScenario, dataSize, operation):
    dataToPlot = []
    perAlgorithmThreads = defaultdict(list)
    perAlgorithmDurations = defaultdict(list)
    #perAlgorithmData = [0] * 4
    for result in results:
        splitResults = result[0].split(delimiter)
        testCaseAlgorithm = splitResults[0]
        testNumberOfThreads = splitResults[1]
        testCaseOperation = splitResults[2]
        testCaseScenario = splitResults[3]
        testCaseDataSize = splitResults[4]
        testDurationInUs = splitResults[5]


        uniqueNumberOfThreads = set()
        threads = []
        durations = []

        # [algo][1, 4, 8, 16][100, 200, 400, 500]

        if testCaseScenario == dataScenario and testCaseDataSize == dataSize and testCaseOperation == operation:

            print("case algorithm: " + testCaseAlgorithm)
            print("number of threads: " + testNumberOfThreads)
            print("case operation: " + testCaseOperation)
            print("case scenario: " + testCaseScenario)
            print("data size: " + testCaseDataSize)
            print("duration in us: " + testDurationInUs)

            perAlgorithmThreads[testCaseAlgorithm].append(testNumberOfThreads)
            perAlgorithmDurations[testCaseAlgorithm].append(float(testDurationInUs) / 1000)

    for algorithm in perAlgorithmThreads:
        print(algorithm)
        dataToPlot.append(go.Bar(name=algorithm, x=perAlgorithmThreads[algorithm], y=perAlgorithmDurations[algorithm]))

    if len(dataToPlot) != 0:
        # style layout
        layout = go.Layout(
            barmode='group',
            title= {
                'text': "Priority Queue Operation: " + operation + " - Input Data: " + dataScenario + " -  Data Size: " + dataSize,
                'x': 0.5
            },
            xaxis=dict(
                title="Number of Threads",
                type='category',
            ),
            yaxis=dict(
                title="Time in Milli Seconds"
            ))

        fig = go.Figure(layout=layout, data=dataToPlot)
        fig.update_layout(xaxis={'categoryorder':'array', 'categoryarray':['1','4','8','16', '32']})
        fileName = dataScenario + "_" + operation + "_"+ dataSize
        offline.plot(figure_or_data=fig, filename=fileName, auto_open=False)


Lines = file.readlines()
testResults = []
delimiter = ","
for line in Lines:
    if re.search("b.Bench*", line):
        # replace white spaces with delimiter
        testResults.append(re.sub("\s+", delimiter, line.strip()))

formattedResults = getFormattedTestResultList(testResults)
print(formattedResults)

for i in ["1000", "10000", "100000", "1000000"]:
    savePlotGroupedResults(formattedResults, "AscendingOrder", i, "EnqOnly")
    savePlotGroupedResults(formattedResults, "AscendingOrder", i, "EnqThenDeq")
    savePlotGroupedResults(formattedResults, "AscendingOrder", i, "DeqOnly")
    savePlotGroupedResults(formattedResults, "AscendingOrder", i, "EnqAndDeqSimultaneously")

    savePlotGroupedResults(formattedResults, "DescendingOrder", i, "EnqOnly")
    savePlotGroupedResults(formattedResults, "DescendingOrder", i, "EnqThenDeq")
    savePlotGroupedResults(formattedResults, "DescendingOrder", i, "DeqOnly")
    savePlotGroupedResults(formattedResults, "DescendingOrder", i, "EnqAndDeqSimultaneously")

    savePlotGroupedResults(formattedResults, "AscendingThenDescendingOrder", i, "EnqOnly")
    savePlotGroupedResults(formattedResults, "AscendingThenDescendingOrder", i, "EnqThenDeq")
    savePlotGroupedResults(formattedResults, "AscendingThenDescendingOrder", i, "DeqOnly")
    savePlotGroupedResults(formattedResults, "AscendingThenDescendingOrder", i, "EnqAndDeqSimultaneously")

    savePlotGroupedResults(formattedResults, "RandomOrder", i, "EnqOnly")
    savePlotGroupedResults(formattedResults, "RandomOrder", i, "EnqThenDeq")
    savePlotGroupedResults(formattedResults, "RandomOrder", i, "DeqOnly")
    savePlotGroupedResults(formattedResults, "RandomOrder", i, "EnqAndDeqSimultaneously")