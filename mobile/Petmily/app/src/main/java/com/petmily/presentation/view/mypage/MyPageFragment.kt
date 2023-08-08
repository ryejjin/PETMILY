package com.petmily.presentation.view.mypage

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseFragment
import com.petmily.databinding.DialogFollowerListBinding
import com.petmily.databinding.FragmentMyPageBinding
import com.petmily.databinding.ItemBoardBinding
import com.petmily.databinding.ItemSearchUserBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.presentation.view.curation.CurationAdapter
import com.petmily.presentation.view.dialog.LogoutDialog
import com.petmily.presentation.view.dialog.WithDrawalDialog
import com.petmily.presentation.view.home.BoardAdapter
import com.petmily.presentation.view.search.SearchUserAdapter
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.PetViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.User
import com.petmily.util.CheckPermission
import com.petmily.util.GalleryUtil

class MyPageFragment :
    BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private val TAG = "petmily_PetInfoFragment"
    private lateinit var mainActivity: MainActivity

    private lateinit var myPetAdapter: MyPetAdapter
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var curationAdapter: CurationAdapter
    private lateinit var followerAdapter: SearchUserAdapter

    private lateinit var galleryUtil: GalleryUtil
    private lateinit var checkPermission: CheckPermission
    
    private val mainViewModel: MainViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val petViewModel: PetViewModel by activityViewModels()

    private val itemList = mutableListOf<Any>() // 아이템 리스트 (NormalItem과 LastItem 객체들을 추가)
    
    // 팔로워 리스트 BottomSheetDialog
    private val followerDialog: Dialog by lazy {
        BottomSheetDialog(mainActivity).apply {
            setContentView(R.layout.dialog_follower_list)
        }
    }
    private val followerDialogBinding: DialogFollowerListBinding by lazy {
        DialogFollowerListBinding.bind(followerDialog.findViewById(R.id.cl_dialog_follower_list))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserInfo()
        initAdapter()
        initPetItemList()
        initTabLayout()
        initBoards()
        initDrawerLayout()
        initImageView()
        initObserver()
        initTextView()
    }

    private fun initUserInfo() = with(binding) {
        userViewModel.mypageInfo.value?.apply {
            // 유저 프로필 이미지
            Glide.with(mainActivity)
                .load(this.userProfileImg)
                .circleCrop()
                .into(ivMypageUserImage)

            // 유저 닉네임
            tvUserName.text = this.userNickname

            // 유저 뱃지
//            Glide.with(mainActivity)
//                .load(this?.user)
//                .circleCrop()
//                .into(ivBadge)

            // 게시글, 팔로우, 팔로잉 수
            tvMypageFeedCnt.text = boardCount.toString()
            tvMypageFollowCnt.text = followerCount.toString()
            tvMypageFollowingCnt.text = followingCount.toString()
        }
    }

    private fun initImageView() = with(binding) {
        // 설정창
        ivMypageOption.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        // 상점
        ivShopIcon.setOnClickListener {
            mainActivity.changeFragment("shop")
        }
    }

    private fun initDrawerLayout() = with(binding) {
        llDrawerProfile.setOnClickListener { // 프로필 수정
            mainActivity.changeFragment("userInfoInput")
        }

        llDrawerPassword.setOnClickListener { // 비밀번호 변경
        }

        llDrawerPoint.setOnClickListener { // 포인트 적립 사용 내역
            // todo API 요청

            mainActivity.changeFragment("pointLog")
        }

        llDrawerSettingNotification.setOnClickListener { // 알림 설정
        }

        llDrawerSettingWithdrawal.setOnClickListener { // 회원 탈퇴
            context?.let { // context가 null이 아닐 때만 다이얼로그를 띄웁니다.
                val dialog = WithDrawalDialog(it, mainViewModel)
                dialog.show()
            }
        }

        llDrawerSettingAppInfo.setOnClickListener { // 앱 정보
        }

        llDrawerLogout.setOnClickListener { // 로그아웃
            context?.let { // context가 null이 아닐 때만 다이얼로그를 띄웁니다.
                val dialog = LogoutDialog(mainActivity, mainViewModel)
                dialog.show()
            }
        }
    }

    private fun initTabLayout() = with(binding) {
        tlMypage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // 선택된 탭에 따라 RecyclerView의 가시성을 변경합니다.
                when (tab.position) {
                    0 -> {
                    }

                    1 -> {
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // 선택 해제된 탭에 따라 RecyclerView의 가시성을 변경합니다.
                // 예를 들어, 선택된 탭이 0번 탭이었고 1번 탭으로 변경되는 경우에는 0번 탭의 RecyclerView를 숨깁니다.
                when (tab.position) {
                    0 -> {
                    }

                    1 -> {
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 탭이 이미 선택된 상태에서 다시 선택되었을 때의 동작을 정의합니다.
                // 필요한 경우 이 함수를 구현하십시오.
            }
        })
    }

    private fun initPetItemList() {
        itemList.clear()
        // NormalItem 등록
        if (!userViewModel.mypageInfo.value?.userPets.isNullOrEmpty()) {
            for (petData in userViewModel.mypageInfo.value!!.userPets) {
                itemList.add(NormalItem(petData))
            }
        }

        // LastItem 등록
        itemList.add(LastItem("Last Item"))
        myPetAdapter.notifyDataSetChanged()
    }

    private fun initAdapter() = with(binding) {
        // pet
        myPetAdapter = MyPetAdapter(itemList, ::onNormalItemClick, ::onLastItemClick)
        rcvMypageMypet.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false)
        rcvMypageMypet.adapter = myPetAdapter

        // 게시글 adapter
        boardAdapter = BoardAdapter(mainActivity).apply {
            setBoardClickListener(object : BoardAdapter.BoardClickListener {
                override fun heartClick(
                    isClicked: Boolean,
                    binding: ItemBoardBinding,
                    board: Board,
                    position: Int,
                ) {
                    if (isClicked) {
                        // 좋아요 등록
                        boardViewModel.registerHeart(board, mainViewModel)
                    } else {
                        // 좋아요 취소
                        boardViewModel.deleteHeart(board, mainViewModel)
                    }
                }
    
                override fun commentClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }
    
                override fun profileClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // TODO("Not yet implemented")
                }
    
                override fun optionClick(binding: ItemBoardBinding, board: Board, position: Int) {
                    // ("Not yet implemented")
                }
            })
        }
        rcvMypageBoard.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
        
        // 팔로워 Adapter
        followerAdapter = SearchUserAdapter().apply {
            setUserClickListener(object : SearchUserAdapter.UserClickListener {
                override fun userClick(binding: ItemSearchUserBinding, user: User, position: Int) {
                    // TODO: 팔로워 클릭 시 사용자 정보로 이동
                }
            })
        }
        followerDialogBinding.rcvFollowerList.apply {
            adapter = followerAdapter
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 피드 게시물 데이터 초기화 TODO: api 통신 코드로 변경
    private fun initBoards() {
        boardViewModel.selectAllBoard(ApplicationClass.sharedPreferences.getString("userEmail") ?: "", mainViewModel)
    }

    // NormalItem 클릭 이벤트 처리 (등록된 펫 정보 보기) - petViewModel
    private fun onNormalItemClick(normalItem: NormalItem) {
        Log.d(TAG, "onNormalItemClick: $normalItem")
        petViewModel.selectPetInfo = normalItem.pet
        mainActivity.changeFragment("petInfo")
    }

    // LastItem 클릭 이벤트 처리 (신규 펫 등록)
    private fun onLastItemClick(lastItem: LastItem) {
        petViewModel.fromPetInfoInputFragment = "MyPageFragment"
        mainActivity.changeFragment("petInfoInput")
    }
    
    private fun initObserver() = with(boardViewModel) {
        // 전체 피드 조회
        selectedBoardList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                // 피드 전체 조회 실패
                Log.d(TAG, "selectedBoardList: 피드 전체 조회 실패")
            } else {
                // 피드 전체 조회 성공
                boardAdapter.setBoards(
                    it.filter { board ->
                        (ApplicationClass.sharedPreferences.getString("userEmail") ?: "") == board.userEmail
                    },
                )
            }
        }
    
        userViewModel.mypageInfo.observe(viewLifecycleOwner) {
            initPetItemList()
        }
    }
    
    private fun initTextView() = with(binding) {
        tvMypageFollowCnt.setOnClickListener {
            // TODO: Adapter에 데이터 삽입
            followerDialog.show()
        }
        tvMypageFollowingCnt.setOnClickListener {
            // TODO: Adapter에 데이터 삽입
            followerDialog.show()
        }
    }
}