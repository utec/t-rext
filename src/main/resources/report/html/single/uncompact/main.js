var report = 
%s
;

function main(data) {
  console.log(data);
  var table = document.getElementById("table").getElementsByTagName('tbody')[0];

  document.getElementById("featureName").innerHTML = data.featureName;
  document.getElementById("featureDescription").innerHTML = data.featureDesc;

  data.scenarioStats.forEach(function(scenarioStat, i) {
    var row = table.insertRow();
    row.id = i;
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    cell1.innerHTML = scenarioStat.name;
    cell2.innerHTML = scenarioStat.asserts;
    cell3.innerHTML = scenarioStat.duration;

    cell1.className = 'test-result-step-command-cell'

   	if (scenarioStat.http === 'passed' && scenarioStat.asserts === 'passed' && scenarioStat.context === 'passed') {
      cell2.className = 'test-result-step-result-cell-ok'
    } else {
      row.className = 'clickable'
      cell2.className = 'test-result-step-result-cell-failure'
      row.onclick = function() {
        var rowId = this.id;
        if (document.getElementById(`httpError_${rowId}`)) {
          var currentDisplay = document.getElementById(`httpError_${rowId}`).style.display;
          document.getElementById(`httpError_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }
        if (document.getElementById(`assertError_${rowId}`)) {
          var currentDisplay = document.getElementById(`assertError_${rowId}`).style.display;
          document.getElementById(`assertError_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }
        if (document.getElementById(`contextError_${rowId}`)) {
          var currentDisplay = document.getElementById(`contextError_${rowId}`).style.display;
          document.getElementById(`contextError_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }
        if (document.getElementById(`globalVariables_${rowId}`)) {
          let currentDisplay = document.getElementById(`globalVariables_${rowId}`).style.display;
          document.getElementById(`globalVariables_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }
        if (document.getElementById(`localHttpRequestVariables_${rowId}`)) {
          var currentDisplay = document.getElementById(`localHttpRequestVariables_${rowId}`).style.display;
          document.getElementById(`localHttpRequestVariables_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }
        if (document.getElementById(`localHttpResponseVariables_${rowId}`)) {
          var currentDisplay = document.getElementById(`localHttpResponseVariables_${rowId}`).style.display;
          document.getElementById(`localHttpResponseVariables_${rowId}`).style.display = (currentDisplay === 'none' ? "" : "none");
        }

      };
    }

    cell3.className = 'test-result-step-command-cell'

    //add errors

    var logAdvice = "More verbose log in java console. Also add -debug to get even more logs."

    if (typeof scenarioStat.httpError !== 'undefined' && scenarioStat.httpError != null) {
      var errorAsString = scenarioStat.httpError.join("\n");
      createErrorRow(table, `httpError_${i}`, `Http error log. (${logAdvice})\n\n${errorAsString}`)
    }

    if (typeof scenarioStat.assertsError !== 'undefined' && scenarioStat.assertsError != null) {
      var errorAsString = scenarioStat.assertsError.join("\n");
      createErrorRow(table, `assertError_${i}`, `Assert error log. (${logAdvice})\n\n${errorAsString}`)
    }

    if (typeof scenarioStat.contextError !== 'undefined' && scenarioStat.contextError != null) {
      var errorAsString = scenarioStat.contextError.join("\n");
      createErrorRow(table, `contextError_${i}`, `Context error log. (${logAdvice})\n\n${errorAsString}`)
    }
    if (scenarioStat.http != "passed" || scenarioStat.asserts != "passed" || scenarioStat.context != "passed") {
		console.log(scenarioStat);
		createErrorRow(table, "globalVariables_" + i, "Global variables \n\n" + JSON.stringify(data.globalVariables, null, 4));
		createErrorRow(table, "localHttpRequestVariables_" + i, "Http Request variables \n\n" + variablesToString(data.localHttpRequestVariables));
		createErrorRow(table, "localHttpResponseVariables_" + i, "Http Response variables \n\n" + variablesToString(data.localHttpResponseVariables));
	}
  });
}

function variablesToString(variables) {
  var finalString;
  if (typeof variables.body !== 'undefined' && variables.body != null) {
    var body = variables.body;
    delete variables.body;
    var variablesAsString = JSON.stringify(variables, null, 4);
    var bodyAsPrettyString = stringifyIfIsJson(body);
    finalString = `${variablesAsString}\n\n\Http Body \n\n${bodyAsPrettyString}`
  } else {
    var variablesAsString = JSON.stringify(variables, null, 4)
    finalString = `${variablesAsString}`
  }

  return finalString;
}

function stringifyIfIsJson(string) {
  try {
    var obj = JSON.parse(string);
    return JSON.stringify(obj, null, 4)
  } catch (err) {
    return string;
  }
}

function createErrorRow(table, rowId, errorText) {
  var errorRow = table.insertRow();
  errorRow.id = rowId;
  errorRow.style = "display:none"
  var errorCell = errorRow.insertCell();
  errorCell.setAttribute("colspan", 3);
  errorCell.className = 'error'
  var pre = document.createElement("pre");
  pre.textContent = errorText;
  errorCell.appendChild(pre);
}

function showChart(data) {

  document.getElementById("date").innerHTML = data.date;

  var total = data.total;
  var passed = (data.passed > 0 ? Math.round((data.passed * 100) / total) : 0);
  var pending = (data.pending > 0 ? Math.round((data.pending * 100) / total) : 0);
  var failed = (data.failed > 0 ? Math.round((data.failed * 100) / total) : 0);

  var ctx = document.getElementById("myChart").getContext('2d');
  var myChart = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: [`Passed ${passed} %`, `Failed ${failed} %`, `Pending ${pending} %`],
      datasets: [{
        backgroundColor: [
          "#2ecc71",
          "#ff2c2c",
          "#95a5a6"
        ],
        data: [passed, failed, pending]
      }]
    }
  });


}

main(report);
showChart(report);