import ArrowDropDownOutlinedIcon from '@mui/icons-material/ArrowDropDownOutlined';
import { styled } from '@mui/material';
import { useState } from 'react';
import axios from 'axios';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

function PetInfo() {
  const StyledArrowDropDownOutlinedIcon = styled(ArrowDropDownOutlinedIcon, {
    name: 'StyledArrowDropDownOutlinedIcon',
    slot: 'Wrapper',
  })({
    color: '#a6a7ab',
    fontSize: 40,
    '&:hover': { color: '#1f90fe' },
  });
  // 초기값 세팅
  const [petname, setPetname] = useState('');
  const [petgener, setPetgender] = useState(0);
  const [petbirth, setPetbirth] = useState(new Date());
  const [petintro, setPetintro] = useState('');
  // // 오류메세지 상태 저장
  // const [petnameMessage, setPetnameMessage] = useState('');
  // const [petgenerMessage, setPetgenderMessage] = useState('');
  // const [petbirthMessage, setPetbirthMessage] = useState('');
  // const [petintroMessage, setPetintroMessage] = useState('');
  // // 유효성 검사
  // const [isPetname, setIsPetname] = useState(false);
  // const [isPetgender, setIsPetgender] = useState(false);
  // const [isPetbirth, setIsPetbirth] = useState(false);
  // const [isPetintro, setIsPetintro] = useState(false);
  const onChangePetname = e => {
    const currentPetname = e.target.value;
    setPetname(currentPetname);
    // if (currentPetname.length < 1 || currentPetname.length > 8) {
    //   setPetnameMessage(
    //     '반려동물 이름은 1글자 이상 8글자 이하로 입력해주세요.',
    //   );
    //   setIsPetname(false);
    // } else {
    //   setPetnameMessage('멋진 이름이네요!');
    //   setIsPetname(true);
    // }
  };
  const onChangePetgender = e => {
    const currentPetgender = e.target.value;
    setPetgender(currentPetgender);
    // if (currentPetgender === 0) {
    //   setPetgenderMessage('반려동물 성별을 선택해주세요.');
    //   setIsPetgender(false);
    // } else {
    //   setPetgenderMessage('머라 적징');
    //   setIsPetgender(true);
    // }
  };
  const onChangePetbirth = e => {
    setPetbirth(e); // Tue Jul 04 2023 16:57:01 GMT+0900 (한국 표준시)
  };
  const onChangePetintro = e => {
    setPetintro(e.target.value);
  };
  const handleSubmit = e => {
    e.preventDefault();
    const petData = {
      petname,
      petgener,
      petbirth,
      petintro,
    };
    axios
      .post('http://192.168.100.208:3000/petinfo', petData)
      .then(response => {
        console.log(response);
      })
      .catch(error => {
        if (error.response) {
          console.log(error.response);
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log(error);
        }
      });
  };
  return (
    <div className="relative bg-white w-full h-[111rem] overflow-y-auto text-left text-[1.25rem] text-gray font-pretendard">
      <div className="absolute top-[calc(50%_-_771px)] left-[calc(50%_-_332px)] rounded-[40px] bg-white w-[41.5rem] flex flex-col p-[2.5rem] box-border items-center justify-start gap-[3.63rem]">
        <div className="relative w-[12.31rem] h-[13.38rem] text-[3.13rem] text-dodgerblue">
          <div className="absolute top-[9.94rem] left-[1.06rem] tracking-[0.3em] font-onemobilepop">
            펫밀리
          </div>
          <img
            className="absolute top-[0rem] left-[0rem] w-[12.31rem] h-[8.76rem]"
            alt=""
            src="/loginlogo.svg"
          />
        </div>
        <b className="self-stretch relative text-[2rem] tracking-[0.01em] leading-[125%]">
          반려동물 설정
        </b>
        <img
          className="relative w-[10.5rem] h-[10.06rem]"
          alt=""
          src="/uploadpetimagecontainer.svg"
        />
        <div className="w-[36rem] flex flex-col items-start justify-center gap-[1rem]">
          <b className="relative text-[1.5rem] tracking-[0.01em] leading-[125%]">
            반려동물 이름
          </b>
          <b className="relative tracking-[0.01em] leading-[125%] flex text-slategray items-center w-[28.5rem] h-[1.56rem] shrink-0">
            반려동물의 이름과 성별을 입력해주세요
          </b>
          <div className="relative self-stretch flex flex-row items-center justify-center gap-[1rem] text-darkgray">
            <input
              className="flex-1 rounded-3xs box-border h-[3rem] flex flex-row px-[1rem] items-center justify-start border-[1.5px] border-solid border-darkgray"
              type="text"
              placeholder="반려동물 이름"
              value={petname}
              onChange={onChangePetname}
            />
            <select
              name=""
              id=""
              value={petgener}
              onChange={onChangePetgender}
              className="appearance-none flex-1 rounded-3xs box-border h-[3rem] px-[1rem] flex flex-row items-center justify-between border-[1.5px] border-solid border-darkgray"
            >
              <option value="0">성별</option>
              <option value="1">수컷</option>
              <option value="2">암컷</option>
            </select>
            <div className="absolute right-2 flex items-center pointer-events-none">
              <StyledArrowDropDownOutlinedIcon className="w-5 h-5 text-gray-400" />
            </div>
          </div>
        </div>
        <div className="flex flex-col items-start justify-center">
          <div className="w-[36rem] flex flex-col items-start justify-start gap-[1rem]">
            <b className="text-[1.5rem] tracking-[0.01em] leading-[125%]">
              생일
            </b>
            <b className="tracking-[0.01em] leading-[125%] flex text-slategray items-center w-[28.5rem] h-[1.56rem] shrink-0">
              반려동물의 생일을 입력해주세요
            </b>
            <DatePicker
              dateFormat="yyyy.MM.dd"
              shouldCloseOnSelect
              selected={petbirth}
              onChange={onChangePetbirth}
              className="w-[17.5rem] rounded-3xs box-border h-[3rem] px-[1rem] items-center justify-between border-[1.5px] border-solid border-darkgray"
            />
          </div>
        </div>
        <div className="w-[36rem] flex flex-col items-start justify-start gap-[1rem]">
          <b className="relative text-[1.5rem] tracking-[0.01em] leading-[125%]">
            소개글
          </b>
          <b className="relative tracking-[0.01em] leading-[125%] flex text-slategray items-center w-[28.5rem] h-[1.56rem] shrink-0">
            함께하고 있는 반려동물을 소개해주세요
          </b>
          <textarea
            name=""
            id=""
            cols="30"
            rows="10"
            placeholder="내용을 작성해주세요"
            value={petintro}
            onChange={onChangePetintro}
            className="rounded-3xs bg-white box-border w-[35.44rem] h-[21.69rem] flex flex-row py-[1.31rem] px-[1.56rem] items-start justify-start text-darkgray border-[1.5px] border-solid border-darkgray"
          />
        </div>
        <div className="relative w-[35.44rem] h-[4.5rem]">
          <button
            type="submit"
            className="absolute top-[0rem] left-[0rem] rounded-[50px] bg-dodgerblue w-[35.44rem] h-[4.5rem]"
            onClick={handleSubmit}
          >
            <b className="absolute top-[1.31rem] left-[calc(50%_-_45.5px)] tracking-[0.01em] leading-[125%] text-[1.5rem] text-white">
              작성 완료
            </b>
          </button>
        </div>
      </div>
    </div>
  );
}

export default PetInfo;