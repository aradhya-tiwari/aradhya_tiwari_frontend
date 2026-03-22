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

for (let s of students) {
  console.log(`${s.name} total marks = ${calculateTotalMarks(s)}`)

}
console.log("---------------------------")

function calculateAverageMarks(student) {
  const total = calculateTotalMarks(student);
  return total / student.marks.length

}

for (let s of students) {
  console.log(`${s.name} average marks: ${calculateAverageMarks(s)}`);
}

console.log("--------------------------------")

function subjectWiseHighestMarks(student) {
  let highestScore = student.marks[0].score;
  let highestSubjects = [student.marks[0].subject];

  for (let i = 1; i < student.marks.length; i++) {
    const currentMark = student.marks[i];

    if (currentMark.score > highestScore) {
      highestScore = currentMark.score;
      highestSubjects = [currentMark.subject];
    } else if (currentMark.score === highestScore) {
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
