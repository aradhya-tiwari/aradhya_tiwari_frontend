const students = [
  {
    name: "Lalit",
    marks: [
      { subject: "Math", score: 78 },
      { subject: "English", score: 82 },
      { subject: "Science", score: 74 },
      { subject: "History", score: 69 },
      { subject: "Computer", score: 88 }
    ],
    attendance: 82
  },
  {
    name: "Rahul",
    marks: [
      { subject: "Math", score: 90 },
      { subject: "English", score: 85 },
      { subject: "Science", score: 80 },
      { subject: "History", score: 76 },
      { subject: "Computer", score: 92 }
    ],
    attendance: 91
  },

  {
    name: "Aradhya",
    marks: [
      { subject: "Math", score: 86 },
      { subject: "English", score: 85 },
      { subject: "Science", score: 94 },
      { subject: "History", score: 82 },
      { subject: "Computer", score: 92 }
    ],
    attendance: 81
  }
];

console.log(students)


function calculateTotalMarks(student) {
  let total = 0;

  for (let i = 0; i < student.marks.length; i++) {
    total += student.marks[i].score;
  }

  return total
}

console.log("Total marks")
students.forEach(function (s) {
  console.log(`${s.name} total marks = ${calculateTotalMarks(s)}`)
});
console.log("---------------------------")

function calculateAverageMarks(student) {
  const total = calculateTotalMarks(student);
  return total / student.marks.length

}

console.log("Average Marks")
students.forEach(s => {

  console.log(`${s.name} average marks: ${calculateAverageMarks(s)}`);
})

console.log("--------------------------------")

function subjectWiseHighestMarks(student) {
  let highestScore = student.marks[0].score;
  let highestSubjects = [student.marks[0].subject];

  for (let i = 1; i < student.marks.length; i++) {
    const currentMark = student.marks[i];

    if (currentMark.score > highestScore) {
      highestScore = currentMark.score;
      highestSubjects = [currentMark.subject];
    }
    // for scenario when multiple highest scored subject exists, updating highestScore is not necessary, already updated
    else if (currentMark.score === highestScore) {
      highestSubjects.push(currentMark.subject);
    }
  }

  return {
    highestScore: highestScore,
    highestSubjects: highestSubjects
  };
}

console.log("Subject-wise highest marks of each student");

for (let s of students) {
  const studentHighest = subjectWiseHighestMarks(s);

  console.log(
    `${s.name} highest in ${studentHighest.highestSubjects.join(", ")}: ${studentHighest.highestScore}`
  );
}

console.log("--------------------------------")

function subjectWiseAverageMarks(studentList) {
  // Subject total map and subject count map subject wise average = subject total/subject count for each student
  const subjectTotals = {};
  const subjectCounts = {};

  for (let i = 0; i < studentList.length; i++) {
    for (let j = 0; j < studentList[i].marks.length; j++) {
      const currentMark = studentList[i].marks[j];

      if (!subjectTotals[currentMark.subject]) {
        subjectTotals[currentMark.subject] = 0;
        subjectCounts[currentMark.subject] = 0;
      }

      subjectTotals[currentMark.subject] += currentMark.score;
      subjectCounts[currentMark.subject] += 1;
    }
  }

  return {
    subjectTotals: subjectTotals,
    subjectCounts: subjectCounts
  };
}

const subjectAverageData = subjectWiseAverageMarks(students);

console.log("Subject wise average scores");

for (let subject in subjectAverageData.subjectTotals) {
  const averageScore =
    subjectAverageData.subjectTotals[subject] / subjectAverageData.subjectCounts[subject];

  console.log(`Average ${subject} Score: ${averageScore.toFixed(1)}`);
}

console.log("--------------------------------")


function overallClassTopper() {
  let topper = {
    name: "",
    marks: ''
  }
  let maxMarks = -1
  for (let s in students) {
    let totalMarks = 0
    students[s].marks.forEach(s => {
      totalMarks += s.score
    })
    if (maxMarks < totalMarks) {
      maxMarks = totalMarks
      topper.name = students[s].name
      topper.marks = maxMarks
    }
  }
  return topper
}

console.log("Class Topper")
let topperMap = overallClassTopper()
console.log(`${topperMap.name} is the class topper with ${topperMap.marks}`)