var report = 
 %s
;

function main(data) {
  console.log(data);
  var table = document.getElementById("table").getElementsByTagName('tbody')[0];

  data.featuresStats.forEach(function(featureStats, i) {
    console.log(featureStats);
    var row = table.insertRow();
    row.id = i;
    var cellFeatureName = row.insertCell(0);
    var cellStatus = row.insertCell(1);
    var cellPassed = row.insertCell(2);
    var cellFailed = row.insertCell(3);
    var cellPending = row.insertCell(4);
    var cellTotal = row.insertCell(5);
    var cellDuration = row.insertCell(6);

    cellFeatureName.innerHTML = `${featureStats.file}: ${featureStats.featureName}`;
    cellPassed.innerHTML = featureStats.passed;
    cellFailed.innerHTML = featureStats.failed;
    cellPending.innerHTML = featureStats.pending;
    cellTotal.innerHTML = featureStats.total;
    cellDuration.innerHTML = featureStats.duration;

    cellFeatureName.className = 'test-result-step-command-cell'
    cellPassed.className = 'test-result-step-command-cell'
    cellFailed.className = 'test-result-step-command-cell'
    cellPending.className = 'test-result-step-command-cell'
    cellTotal.className = 'test-result-step-command-cell'
    cellDuration.className = 'test-result-step-command-cell'

    if (featureStats.total === featureStats.passed) {
      cellStatus.className = 'test-result-step-result-cell-ok'
      cellStatus.innerHTML = "success"
    } else {
      // row.className = 'clickable'
      cellStatus.className = 'test-result-step-result-cell-failure'
      cellStatus.innerHTML = "error"
    }
  });
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